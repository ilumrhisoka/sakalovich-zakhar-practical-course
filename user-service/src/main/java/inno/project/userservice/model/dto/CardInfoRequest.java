package inno.project.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "Request for card information.")
public record CardInfoRequest(
        @NotNull
        @Pattern(regexp = "\\d{16}")
        @Schema(description = "16-digit card number.", example = "1234567812345678")
        String number,

        @NotNull
        @Schema(description = "Expiration date of the card.", example = "2030-01-01T00:00:00")
        LocalDateTime expirationDate,

        @NotNull
        @Schema(description = "Name of the card holder.", example = "John Doe")
        String holder,

        @NotNull
        @Schema(description = "Identifier of user.", example = "1")
        Long userId
)  implements Serializable{
}