package inno.project.userservice.unit.service;

import inno.project.userservice.exception.user.DuplicateEmailException;
import inno.project.userservice.exception.user.UserNotFoundException;
import inno.project.userservice.mapper.UserMapper;
import inno.project.userservice.model.dto.UserRequest;
import inno.project.userservice.model.dto.UserResponse;
import inno.project.userservice.model.entity.User;
import inno.project.userservice.repository.UserRepository;
import inno.project.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private final LocalDateTime MOCK_BIRTH_DATE = LocalDateTime.of(1990, 1, 1, 0, 0);

    private UserRequest userRequest;
    private User userEntity;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest("John", "Doe", "john@example.com", MOCK_BIRTH_DATE);

        userEntity = new User();
        userEntity.setId(1L);
        userEntity.setName("John");
        userEntity.setSurname("Doe");
        userEntity.setEmail("john@example.com");
        userEntity.setBirthDate(MOCK_BIRTH_DATE);
        userEntity.setVersion(1L);
        userEntity.setCreatedAt(LocalDateTime.now());
        userEntity.setUpdatedAt(LocalDateTime.now());

        userResponse = UserResponse.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("john@example.com")
                .birthDate(MOCK_BIRTH_DATE)
                .build();
    }

    @Test
    void createUser_Success() {
        when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.empty());
        when(userMapper.toEntity(userRequest)).thenReturn(userEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDto(userEntity)).thenReturn(userResponse);

        UserResponse result = userService.createUser(userRequest);

        assertEquals(userResponse.id(), result.id());
        verify(userRepository).save(userEntity);
    }

    @Test
    void createUser_DuplicateEmail_ThrowsException() {
        when(userRepository.findByEmail(userRequest.email())).thenReturn(Optional.of(userEntity));

        assertThrows(DuplicateEmailException.class, () -> userService.createUser(userRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(userResponse);

        UserResponse result = userService.getUserById(1L);

        assertEquals(userResponse.id(), result.id());
    }

    @Test
    void getUserById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    void getUsersById_Success() {
        List<Long> ids = List.of(1L, 2L);

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Jane");
        user2.setSurname("Smith");
        user2.setEmail("jane@example.com");
        user2.setBirthDate(MOCK_BIRTH_DATE);

        UserResponse response2 = UserResponse.builder()
                .id(2L)
                .name("Jane")
                .surname("Smith")
                .email("jane@example.com")
                .birthDate(MOCK_BIRTH_DATE)
                .build();

        when(userRepository.findAllById(ids)).thenReturn(List.of(userEntity, user2));
        when(userMapper.toDto(userEntity)).thenReturn(userResponse);
        when(userMapper.toDto(user2)).thenReturn(response2);

        List<UserResponse> result = userService.getUsersById(ids);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(2L, result.get(1).id());
    }

    @Test
    void getUsersById_EmptyList_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> userService.getUsersById(List.of()));
    }

    @Test
    void getUsersById_NotFound_ThrowsException() {
        List<Long> ids = List.of(99L);
        when(userRepository.findAllById(ids)).thenReturn(List.of());

        assertThrows(UserNotFoundException.class, () -> userService.getUsersById(ids));
    }

    @Test
    void getUserByEmail_Success() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(userEntity));
        when(userMapper.toDto(userEntity)).thenReturn(userResponse);

        UserResponse result = userService.getUserByEmail("john@example.com");

        assertEquals("john@example.com", result.email());
    }

    @Test
    void getUserByEmail_NotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail("missing@example.com"));
    }

    @Test
    void updateUser_Success() {
        UserRequest updateRequest = new UserRequest("John", "Doe", "new@example.com", MOCK_BIRTH_DATE);

        User updatedEntity = new User();
        updatedEntity.setId(1L);
        updatedEntity.setName("John");
        updatedEntity.setSurname("Doe");
        updatedEntity.setEmail("new@example.com");
        updatedEntity.setBirthDate(MOCK_BIRTH_DATE);

        UserResponse updatedResponse = UserResponse.builder()
                .id(1L)
                .name("John")
                .surname("Doe")
                .email("new@example.com")
                .birthDate(MOCK_BIRTH_DATE)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());

        doAnswer(invocation -> {
            userEntity.setEmail("new@example.com");
            return null;
        }).when(userMapper).update(userEntity, updateRequest);

        when(userRepository.save(userEntity)).thenReturn(updatedEntity);
        when(userMapper.toDto(updatedEntity)).thenReturn(updatedResponse);

        UserResponse result = userService.updateUser(1L, updateRequest);

        assertEquals("new@example.com", result.email());
    }

    @Test
    void updateUser_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(99L, userRequest));
    }

    @Test
    void updateUser_DuplicateEmail_ThrowsException() {
        UserRequest updateRequest = new UserRequest("John", "Doe", "existing@example.com", MOCK_BIRTH_DATE);
        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setName("Jane");
        otherUser.setSurname("Smith");
        otherUser.setEmail("existing@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(otherUser));

        assertThrows(DuplicateEmailException.class, () -> userService.updateUser(1L, updateRequest));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_NotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(99L));
    }

    @Test
    void getEntityById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));

        User result = userService.getEntityById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getEntityById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getEntityById(99L));
    }
}
