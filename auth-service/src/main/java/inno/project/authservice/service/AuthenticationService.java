package inno.project.authservice.service;
import inno.project.authservice.exception.InvalidCredentialsException;
import inno.project.authservice.exception.TokenValidationException;
import inno.project.authservice.model.dto.AuthenticationRequest;
import inno.project.authservice.model.dto.JwtResponse;
import inno.project.authservice.model.entity.Credential;
import inno.project.authservice.model.entity.RefreshToken;
import inno.project.authservice.repository.CredentialsRepository;
import inno.project.authservice.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final CredentialsRepository credentialsRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private static final String INVALID_CREDENTIALS_MSG = "Invalid email or password.";
    private static final String INVALID_TOKEN_MSG = "Invalid or expired refresh token.";

    @Transactional
    public JwtResponse authenticate(AuthenticationRequest authenticationRequest) {
        Credential credential = credentialsRepository.findByEmail(authenticationRequest.email())
                .orElseThrow(() -> new InvalidCredentialsException(INVALID_CREDENTIALS_MSG));

        if(!passwordEncoder.matches(authenticationRequest.password(), credential.getPassword())) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS_MSG);
        }

        return generateAndSaveTokens(credential);
    }

    @Transactional
    public JwtResponse refreshToken(String oldRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(oldRefreshToken)
                .orElseThrow(() -> new TokenValidationException(INVALID_TOKEN_MSG));

        if(refreshToken.getExpiryDate().isBefore(Instant.now())){
            refreshTokenRepository.delete(refreshToken);
            throw new TokenValidationException(INVALID_TOKEN_MSG);
        }

        Credential credential = refreshToken.getCredential();

        refreshTokenRepository.delete(refreshToken);

        return generateAndSaveTokens(credential);
    }

    private JwtResponse generateAndSaveTokens(Credential credential) {
        String subject = credential.getEmail();

        String accessToken = jwtService.generateAccessToken(subject);
        String newRefreshToken = jwtService.generateRefreshToken(subject);

        Instant refreshTokenExpiry = jwtService.extractClaim(newRefreshToken, Claims::getExpiration).toInstant();

        RefreshToken refreshToken = RefreshToken.builder()
                .token(newRefreshToken)
                .expiryDate(refreshTokenExpiry)
                .credential(credential)
                .build();

        refreshTokenRepository.save(refreshToken);

        return new JwtResponse(accessToken, newRefreshToken);
    }
}