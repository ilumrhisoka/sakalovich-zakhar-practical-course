package inno.project.authservice.exception;

public class DuplicateEmailException extends AuthenticationServiceException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
