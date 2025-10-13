package inno.project.userservice.exception.user;

import inno.project.userservice.exception.UserServiceException;

public class UserNotFoundException extends UserServiceException {
    public UserNotFoundException(String message) {
        super(message);
    }
}