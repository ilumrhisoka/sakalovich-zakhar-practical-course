package inno.project.authservice.exception;

public class InvalidCredentialsException extends AuthenticationServiceException {
    public InvalidCredentialsException(String message) {
        super(message);
    }
}