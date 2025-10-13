package inno.project.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "Request for a user.")
public record UserRequest(

        @NotNull
        @Size(min = 2, max = 50)
        @Schema(description = "User's first name.", example = "John")
        String name,

        @NotNull
        @Size(min = 2, max = 50)
        @Schema(description = "User's surname.", example = "Doe")
        String surname,

        @NotNull
        @Email
        @Size(min = 4, max = 50)
        @Schema(description = "User's email.", example = "john.doe@example.com")
        String email,

        @NotNull
        @Schema(description = "User's date of birth.", example = "1999-01-01T00:00:00")
        LocalDateTime birthDate

) implements Serializable {
}