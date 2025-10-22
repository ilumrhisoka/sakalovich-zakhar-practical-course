package inno.project.authservice.service;

import inno.project.authservice.exception.DuplicateEmailException;
import inno.project.authservice.model.dto.JwtResponse;
import inno.project.authservice.model.dto.RegistrationRequest;
import inno.project.authservice.model.entity.Credential;
import inno.project.authservice.model.entity.RefreshToken;
import inno.project.authservice.model.entity.UserRole;
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
public class RegistrationService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    private final CredentialsRepository credentialsRepository;
    private final JwtService jwtService;

    @Transactional
    public JwtResponse register(RegistrationRequest registrationRequest) {
        if(credentialsRepository.existsByEmail(registrationRequest.email())) {
            throw new DuplicateEmailException("Email already exists: "+ registrationRequest.email());
        }

        String encodedPassword = passwordEncoder.encode(registrationRequest.password());

        Credential credential = Credential.builder()
                .email(registrationRequest.email())
                .password(encodedPassword)
                .role(UserRole.ROLE_USER)
                .build();

        Credential savedUser = credentialsRepository.save(credential);
        return generateAndSaveTokens(savedUser);
    }

    private JwtResponse generateAndSaveTokens(Credential credential) {
        String subject = credential.getEmail();


        String accessToken = jwtService.generateAccessToken(credential.getEmail());

        String newRefreshToken = jwtService.generateRefreshToken(credential.getEmail());

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
