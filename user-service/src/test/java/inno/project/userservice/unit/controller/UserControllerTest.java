package inno.project.userservice.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import inno.project.userservice.controller.UserController;
import inno.project.userservice.exception.GlobalExceptionHandler;
import inno.project.userservice.exception.user.UserNotFoundException;
import inno.project.userservice.model.dto.UserRequest;
import inno.project.userservice.model.dto.UserResponse;
import inno.project.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    private final LocalDateTime MOCK_BIRTH_DATE = LocalDateTime.of(1990, 1, 1, 0, 0);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    @Test
    void createUser_ReturnsCreatedUser() throws Exception {
        UserRequest request = new UserRequest("TestName", "TestSurname", "test@example.com", MOCK_BIRTH_DATE);
        UserResponse response = createMockUserResponse(1L, "TestName", "TestSurname", "test@example.com", MOCK_BIRTH_DATE);

        Mockito.when(userService.createUser(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("TestName"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getUser_ReturnsValidResponse() throws Exception {
        UserResponse response = createMockUserResponse(1L, "TestName", "TestSurname", "test@example.com", MOCK_BIRTH_DATE);

        Mockito.when(userService.getUserById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("TestName"))
                .andExpect(jsonPath("$.surname").value("TestSurname"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void getUsers_ReturnsListOfUsers() throws Exception {
        UserResponse user1 = createMockUserResponse(1L, "TestName1", "TestSurname1", "test1@example.com", MOCK_BIRTH_DATE);
        UserResponse user2 = createMockUserResponse(2L, "TestName2", "TestSurname2", "test2@example.com", MOCK_BIRTH_DATE);

        Mockito.when(userService.getUsersById(List.of(1L, 2L))).thenReturn(List.of(user1, user2));

        mockMvc.perform(get("/api/users")
                        .param("ids", "1", "2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    void getUserByEmail_ReturnsUser() throws Exception {
        UserResponse response = createMockUserResponse(1L, "TestName", "TestSurname", "test@example.com", MOCK_BIRTH_DATE);

        Mockito.when(userService.getUserByEmail("test@example.com")).thenReturn(response);

        mockMvc.perform(get("/api/users/email")
                        .param("email", "test@example.com"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void updateUser_ReturnsUpdatedUser() throws Exception {
        UserRequest request = new UserRequest("UpdatedName", "UpdatedSurname", "updated@example.com", MOCK_BIRTH_DATE);
        UserResponse response = createMockUserResponse(1L, "UpdatedName", "UpdatedSurname", "updated@example.com", MOCK_BIRTH_DATE);

        Mockito.when(userService.updateUser(eq(1L), any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("UpdatedName"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void deleteUser_ReturnsNoContent() throws Exception {
        Mockito.doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getUser_ReturnsNotFound() throws Exception {
        Mockito.when(userService.getUserById(99L))
                .thenThrow(new UserNotFoundException("User not found"));
        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserByEmail_ReturnsNotFound() throws Exception {
        Mockito.when(userService.getUserByEmail("missing@example.com"))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/api/users/email")
                        .param("email", "missing@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_InvalidRequest_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_ReturnsNotFound() throws Exception {
        UserRequest request = new UserRequest("Name", "Surname", "missing@example.com", MOCK_BIRTH_DATE);

        Mockito.when(userService.updateUser(eq(99L), any(UserRequest.class)))
                .thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(put("/api/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_ReturnsNotFound() throws Exception {
        Mockito.doThrow(new UserNotFoundException("User not found"))
                .when(userService).deleteUser(99L);

        mockMvc.perform(delete("/api/users/99"))
                .andExpect(status().isNotFound());
    }



    private UserResponse createMockUserResponse(Long id, String name, String surname, String email, LocalDateTime birthDate) {
        return UserResponse.builder()
                .id(id)
                .name(name)
                .surname(surname)
                .email(email)
                .birthDate(birthDate)
                .build();
    }
}
