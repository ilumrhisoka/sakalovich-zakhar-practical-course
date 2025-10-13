package inno.project.userservice.exception.card;

import inno.project.userservice.exception.UserServiceException;

public class DuplicateCardNumberException extends UserServiceException {
    public DuplicateCardNumberException(String message) {
        super(message);
    }
}