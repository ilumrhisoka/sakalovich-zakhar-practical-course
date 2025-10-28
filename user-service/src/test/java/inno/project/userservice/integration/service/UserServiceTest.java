package inno.project.userservice.integration.service;

import inno.project.userservice.config.TestContainersConfig;
import inno.project.userservice.exception.user.DuplicateEmailException;
import inno.project.userservice.exception.user.UserNotFoundException;
import inno.project.userservice.model.dto.UserRequest;
import inno.project.userservice.model.dto.UserResponse;
import inno.project.userservice.repository.UserRepository;
import inno.project.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class UserServiceTest extends TestContainersConfig {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    private UserRequest testUserRequest;

    @BeforeEach
    void setUp() {
        testUserRequest = new UserRequest(
                "John",
                "Doe",
                "john.doe@example.com", LocalDateTime.now().minusYears(20)
        );
    }

    @Test
    void createUser_success() {
        UserResponse response = userService.createUser(testUserRequest);

        assertThat(response).isNotNull();
        assertThat(response.email()).isEqualTo(testUserRequest.email());
        assertThat(userRepository.existsById(response.id())).isTrue();
    }

    @Test
    void createUser_duplicateEmail_throwsException() {
        userService.createUser(testUserRequest);

        assertThatThrownBy(() -> userService.createUser(testUserRequest))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email already exists");
    }

    @Test
    void getUserById_success() {
        UserResponse created = userService.createUser(testUserRequest);

        UserResponse fetched = userService.getUserById(created.id());

        assertThat(fetched).isNotNull();
        assertThat(fetched.email()).isEqualTo(testUserRequest.email());
    }

    @Test
    void getUserById_notFound_throwsException() {
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("not found");
    }

    @Test
    void getUserByEmail_success() {
        userService.createUser(testUserRequest);

        UserResponse fetched = userService.getUserByEmail(testUserRequest.email());

        assertThat(fetched).isNotNull();
        assertThat(fetched.email()).isEqualTo(testUserRequest.email());
    }

    @Test
    void getUserByEmail_notFound_throwsException() {
        assertThatThrownBy(() -> userService.getUserByEmail("nonexistent@example.com"))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void getUsersById_success() {
        UserResponse user1 = userService.createUser(testUserRequest);
        UserResponse user2 = userService.createUser(new UserRequest("Alice", "Smith", "alice@example.com", LocalDateTime.now().minusYears(20)));

        List<UserResponse> users = userService.getUsersById(List.of(user1.id(), user2.id()));

        assertThat(users).hasSize(2);
    }

    @Test
    void getUsersById_emptyList_throwsException() {
        assertThatThrownBy(() -> userService.getUsersById(List.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be empty");
    }

    @Test
    void getUsersById_notFound_throwsException() {
        assertThatThrownBy(() -> userService.getUsersById(List.of(999L, 1000L)))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("No users found");
    }

    @Test
    void updateUser_success() {
        UserResponse created = userService.createUser(testUserRequest);

        UserRequest updateRequest = new UserRequest("Johnny", "Doe", "updated@example.com", LocalDateTime.now().minusYears(20));
        UserResponse updated = userService.updateUser(created.id(), updateRequest);

        assertThat(updated.email()).isEqualTo("updated@example.com");
        assertThat(updated.name()).isEqualTo("Johnny");
    }

    @Test
    void updateUser_duplicateEmail_throwsException() {
        UserResponse firstUser = userService.createUser(testUserRequest);
        UserResponse anotherUser = userService.createUser(
                new UserRequest("Alice", "Smith", "alice@example.com", LocalDateTime.now().minusYears(20))
        );

        UserRequest updateRequest = new UserRequest("Bob", "Smith", testUserRequest.email(), LocalDateTime.now().minusYears(20));

        assertThatThrownBy(() -> userService.updateUser(anotherUser.id(), updateRequest))
                .isInstanceOf(DuplicateEmailException.class)
                .hasMessageContaining("Email already exists");
    }


    @Test
    void updateUser_notFound_throwsException() {
        UserRequest updateRequest = new UserRequest("X", "Y", "XY@example.com", LocalDateTime.now().minusYears(20));

        assertThatThrownBy(() -> userService.updateUser(999L, updateRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void deleteUser_success() {
        UserResponse created = userService.createUser(testUserRequest);

        userService.deleteUser(created.id());

        assertThat(userRepository.existsById(created.id())).isFalse();
    }

    @Test
    void deleteUser_notFound_throwsException() {
        assertThatThrownBy(() -> userService.deleteUser(999L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("User not found");
    }


    @Test
    void cacheable_getUserById_hitsCache() {
        UserResponse created = userService.createUser(testUserRequest);

        UserResponse firstCall = userService.getUserById(created.id());

        UserResponse secondCall = userService.getUserById(created.id());

        assertThat(firstCall).isEqualTo(secondCall);

        // Проверяем, что объект реально есть в кэше
        var cache = cacheManager.getCache(UserService.USER_CACHE_NAME);
        assertThat(cache).isNotNull();

        UserResponse cached = cache.get(created.id(), UserResponse.class);
        assertThat(cached).isNotNull();
        assertThat(cached).isEqualTo(firstCall);
    }

    @Test
    void cachePut_updateUser_updatesCache() {
        UserResponse created = userService.createUser(testUserRequest);

        UserRequest updateRequest = new UserRequest(
                "John", "Doe", "updated@example.com", LocalDateTime.now().minusYears(20)
        );
        UserResponse updated = userService.updateUser(created.id(), updateRequest);

        var cache = cacheManager.getCache(UserService.USER_CACHE_NAME);
        assertThat(cache).isNotNull();

        UserResponse cached = cache.get(created.id(), UserResponse.class);
        assertThat(cached).isNotNull();
        assertThat(cached.email()).isEqualTo("updated@example.com");
    }

    @Test
    void cacheEvict_deleteUser_removesFromCache() {
        UserResponse created = userService.createUser(testUserRequest);

        userService.getUserById(created.id());

        userService.deleteUser(created.id());

        var cache = cacheManager.getCache(UserService.USER_CACHE_NAME);
        assertThat(cache).isNotNull();

        UserResponse cached = cache.get(created.id(), UserResponse.class);
        assertThat(cached).isNull();
    }

}
