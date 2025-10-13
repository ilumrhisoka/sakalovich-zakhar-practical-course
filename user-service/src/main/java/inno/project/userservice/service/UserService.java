package inno.project.userservice.service;

import inno.project.userservice.exception.user.DuplicateEmailException;
import inno.project.userservice.exception.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import inno.project.userservice.mapper.UserMapper;
import inno.project.userservice.model.dto.UserRequest;
import inno.project.userservice.model.dto.UserResponse;
import inno.project.userservice.model.entity.User;
import inno.project.userservice.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService{

    public static final String USER_CACHE_NAME = "users";

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    @CachePut(value = USER_CACHE_NAME, key = "#result.id()")
    public UserResponse createUser(UserRequest userRequest){
        if (userRepository.findByEmail(userRequest.email()).isPresent()) {
            throw new DuplicateEmailException("Email already exists: " + userRequest.email());
        }
        User saved = userRepository.save(userMapper.toEntity(userRequest));
        return userMapper.toDto(saved);
    }

    @Cacheable(value = USER_CACHE_NAME, key = "#id")
    public UserResponse getUserById(Long id){
        return userRepository.findById(id).map(user -> {;
            return userMapper.toDto(user);
        }).orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    public List<UserResponse> getUsersById(List<Long> ids){
        if (ids == null || ids.isEmpty()) {
            throw new IllegalArgumentException("List of IDs must not be empty");
        }
        List<User> users = userRepository.findAllByIds(ids);
        if (users.isEmpty()) {
            throw new UserNotFoundException("No users found for given ids: " + ids);
        }
        return users.stream().map(userMapper::toDto).toList();
    }

    @Cacheable(value = USER_CACHE_NAME, key = "#email")
    public UserResponse getUserByEmail(String email){
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserNotFoundException("User not found for email: " + email));
    }

    @Transactional
    @CachePut(value = USER_CACHE_NAME, key = "#id")
    public UserResponse updateUser(Long id, UserRequest userRequest){
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found for id: " + id));
        if (!existing.getEmail().equals(userRequest.email())) {
            userRepository.findByEmail(userRequest.email())
                    .ifPresent(foundUser -> {
                        throw new DuplicateEmailException("Email already exists: " + userRequest.email());
                    });
        }
        userMapper.update(existing, userRequest);
        User saved = userRepository.save(existing);

        return userMapper.toDto(saved);
    }

    @Transactional
    @CacheEvict(value = USER_CACHE_NAME, key = "#id")
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found for id: " + id);
        }
        userRepository.deleteById(id);
    }

    public User getEntityById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }
}