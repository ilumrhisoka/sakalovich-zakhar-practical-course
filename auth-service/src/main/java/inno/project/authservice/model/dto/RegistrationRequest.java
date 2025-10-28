package inno.project.authservice.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegistrationRequest (
    @NotNull
    @Email
    String email,

    @NotNull
    @Size(min=8)
    String password,

    @NotNull
    String name,

    @NotNull
    String surname,

    @NotNull
    LocalDate birthDate
) {}
