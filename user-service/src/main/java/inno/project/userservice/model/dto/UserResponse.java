package inno.project.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Schema(description = "Response of user information.")
public record UserResponse(

        @Schema(description = "Identifier.", example = "1")
        Long id,

        @Schema(description = "User's first name.", example = "John")
        String name,

        @Schema(description = "User's surname.", example = "Doe")
        String surname,

        @Schema(description = "User's email.", example = "john.doe@example.com")
        String email,

        @Schema(description = "User's date of birth.", example = "1999-01-01T00:00:00")
        LocalDateTime birthDate

) implements Serializable {
}