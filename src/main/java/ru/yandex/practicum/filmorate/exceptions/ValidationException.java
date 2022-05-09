package ru.yandex.practicum.filmorate.exceptions;

public class ValidationException extends RuntimeException {
    private final String field;
    public ValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
