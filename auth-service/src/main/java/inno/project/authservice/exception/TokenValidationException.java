package inno.project.authservice.exception;

public class TokenValidationException extends AuthenticationServiceException {
    public TokenValidationException(String message) {
        super(message);
    }
}