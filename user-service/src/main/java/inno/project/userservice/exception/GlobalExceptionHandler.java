package inno.project.userservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import inno.project.userservice.exception.card.CardInfoNotFoundException;
import inno.project.userservice.exception.card.DuplicateCardNumberException;
import inno.project.userservice.exception.card.ExpiredCardException;
import inno.project.userservice.exception.user.DuplicateEmailException;
import inno.project.userservice.exception.user.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDto> handleUserNotFoundException(
            UserNotFoundException ex, HttpServletRequest request) {
        log.error("NoUserFoundException: {}", ex.getMessage(), ex);
        ErrorDto ErrorDto = new ErrorDto(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(ErrorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CardInfoNotFoundException.class)
    public ResponseEntity<ErrorDto> handleCardInfoNotFoundException(
            CardInfoNotFoundException ex, HttpServletRequest request) {
        log.error("NoCardInfoFoundException: {}", ex.getMessage(), ex);
        ErrorDto ErrorDto = new ErrorDto(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(ErrorDto, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateCardNumberException.class)
    public ResponseEntity<ErrorDto> handleDuplicateCardNumberException(
            DuplicateCardNumberException ex, HttpServletRequest request) {
        log.error("DuplicateCardNumberException: {}", ex.getMessage(), ex);
        ErrorDto ErrorDto = new ErrorDto(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(ErrorDto, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ExpiredCardException.class)
    public ResponseEntity<ErrorDto> handleExpiredCardException(
            ExpiredCardException ex, HttpServletRequest request) {
        log.error("ExpiredCardException: {}", ex.getMessage(), ex);
        ErrorDto ErrorDto = new ErrorDto(
                LocalDateTime.now(),
                HttpStatus.GONE.value(),
                HttpStatus.GONE.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(ErrorDto, HttpStatus.GONE);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorDto> handleDuplicateEmailException(
            DuplicateEmailException ex, HttpServletRequest request) {
        log.error("DuplicateEmailException: {}", ex.getMessage(), ex);
        ErrorDto ErrorDto = new ErrorDto(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return new ResponseEntity<>(ErrorDto, HttpStatus.CONFLICT);
    }
}