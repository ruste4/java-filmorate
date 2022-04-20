package ru.yandex.practicum.filmorate.exceptions;

public class ResponseWhenException {
    private String message;

    public ResponseWhenException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
