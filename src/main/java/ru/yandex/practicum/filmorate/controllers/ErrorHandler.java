package ru.yandex.practicum.filmorate.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({FilmNotFoundException.class, UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(RuntimeException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(ValidationException e) {
        return Map.of(e.getField(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

//    @ExceptionHandler
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ErrorResponse handleOtherErrors(Throwable e) {
//        return new ErrorResponse("Internal server error");
//    }
}

class ErrorResponse {
    private final String error;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public ErrorResponse(String error, String description) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}