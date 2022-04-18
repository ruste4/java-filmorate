package ru.yandex.practicum.filmorate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exceptions.*;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Slf4j
public class DefaultAdvice {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ResponseWhenException> handleValidationException(
            HttpServletRequest req,
            ValidationException e) {
        log.warn("Request: " + req.getRequestURL() + " raised " + e);

        ResponseWhenException response = new ResponseWhenException(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler({FilmNotFoundException.class, UserNotFoundException.class})
    public ResponseEntity<ResponseWhenException> handleNotFoundException(HttpServletRequest req, Exception e) {
        log.warn("Request: " + req.getRequestURL() + " raised " + e);

        ResponseWhenException response = new ResponseWhenException(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({FilmAlreadyExistException.class, UserAlreadyExistException.class})
    public ResponseEntity<ResponseWhenException> handleAlreadyExistException(HttpServletRequest req, Exception e) {
        log.warn("Request: " + req.getRequestURL() + " raised " + e);

        ResponseWhenException response = new ResponseWhenException(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.ALREADY_REPORTED);
    }
}
