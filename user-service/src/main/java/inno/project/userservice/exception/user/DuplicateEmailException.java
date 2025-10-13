package inno.project.userservice.exception.user;

import inno.project.userservice.exception.UserServiceException;

public class DuplicateEmailException extends UserServiceException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}