package inno.project.userservice.controller;

import inno.project.userservice.model.dto.UserRequest;
import inno.project.userservice.model.dto.UserResponse;
import inno.project.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for user information")
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new user",
            description = "Creates a new user and returns information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
    })
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Validated @RequestBody UserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userRequest));
    }

    @PreAuthorize("hasRole('ADMIN') or #id == principal.username")
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID",
            description = "Retrieves user information for user ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
    })
    public ResponseEntity<UserResponse> getUser(@PathVariable long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @Operation(summary = "Get users by IDs",
            description = "Retrieves information for multiple users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "One or more users not found"),
    })
    public ResponseEntity<List<UserResponse>> getUsers(@RequestParam List<Long> ids) {
        return ResponseEntity.ok(userService.getUsersById(ids));
    }

    @PreAuthorize("hasRole('ADMIN') or @userService.getUserByEmail(#email).id.toString() == principal.username")
    @GetMapping("/email")
    @Operation(summary = "Get user by email",
            description = "Retrieves user information for the email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
    })
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PreAuthorize("hasRole('ADMIN') or #id.toString() == principal.username")
    @PutMapping("/{id}")
    @Operation(summary = "Update user by ID",
            description = "Updates the information of a specified user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
    })
    public ResponseEntity<UserResponse> updateUser(@PathVariable long id,
                                                   @RequestBody UserRequest userRequest) {
        return ResponseEntity.ok(userService.updateUser(id, userRequest));
    }

    @PreAuthorize("hasRole('ADMIN') or #id.toString() == principal.username")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user by ID",
            description = "Deletes the user specified by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
    })
    public ResponseEntity<UserResponse> deleteUser(@PathVariable long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
