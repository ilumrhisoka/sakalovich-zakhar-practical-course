package inno.project.userservice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Builder
@Schema(description = "Response for card information.")
public record CardInfoResponse(

        @Schema(description = "Identifier of the card.", example = "1")
        Long id,

        @Schema(description = "16-digit card number.", example = "1234567812345678")
        String number,

        @Schema(description = "Name of the card holder.", example = "John Doe")
        String holder,

        @Schema(description = "Expiration date.", example = "2030-01-01T00:00:00")
        LocalDateTime expirationDate,

        @Schema(description = "Identifier.", example = "1")
        Long userId
) implements Serializable {
}