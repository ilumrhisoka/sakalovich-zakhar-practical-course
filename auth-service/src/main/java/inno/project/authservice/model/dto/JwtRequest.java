package inno.project.authservice.model.dto;

import jakarta.validation.constraints.NotNull;

public record JwtRequest (
        @NotNull
        String token
){}
