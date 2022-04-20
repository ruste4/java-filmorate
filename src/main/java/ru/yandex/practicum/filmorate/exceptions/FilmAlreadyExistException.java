package ru.yandex.practicum.filmorate.exceptions;

public class FilmAlreadyExistException extends Exception {
    public FilmAlreadyExistException(String message) {
        super(message);
    }
}
