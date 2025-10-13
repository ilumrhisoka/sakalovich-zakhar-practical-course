package inno.project.userservice.exception.card;

import inno.project.userservice.exception.UserServiceException;

public class CardInfoNotFoundException extends UserServiceException {
    public CardInfoNotFoundException(String message) {
        super(message);
    }
}