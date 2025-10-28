package inno.project.authservice.service;

import inno.project.authservice.exception.TokenValidationException; // Импорт
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException; // Импорт для ловли ошибок JWT
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.lang.Function;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    @Value("${secret.key}")
    private String secretKey;

    @Value("${secret.ttl.access}")
    private long accessTokenTtl;

    @Value("${secret.ttl.refresh}")
    private long refreshTokenTtl;

    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String generateAccessToken(String subject) {
        return buildToken(subject, accessTokenTtl);
    }

    public String generateRefreshToken(String subject) {
        return buildToken(subject, refreshTokenTtl);
    }

    private String buildToken(String subject, long ttl) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(ttl);

        return Jwts.builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        SecretKey key = getSigningKey();

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Boolean isTokenValid(String token) {
        try{
            extractAllClaims(token);
            return true;
        }
        catch(JwtException e){
            throw new TokenValidationException("Access token is invalid or expired: " + e.getMessage());
        }
    }
}