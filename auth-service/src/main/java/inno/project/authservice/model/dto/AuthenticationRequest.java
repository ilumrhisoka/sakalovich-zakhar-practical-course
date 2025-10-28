package inno.project.authservice.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AuthenticationRequest (
        @NotNull
        @Email
        String email,

        @NotNull
        @Size(min=8)
        String password
) {}