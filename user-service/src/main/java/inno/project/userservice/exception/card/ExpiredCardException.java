package inno.project.userservice.exception.card;

import inno.project.userservice.exception.UserServiceException;

public class ExpiredCardException extends UserServiceException {
    public ExpiredCardException(String message) {
        super(message);
    }
}