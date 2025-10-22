package inno.project.authservice.model.dto;

public record JwtResponse (
        String accessToken,
        String refreshToken
){
}
