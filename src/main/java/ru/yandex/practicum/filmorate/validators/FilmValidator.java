package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;

import java.time.LocalDate;

public class FilmValidator {
    public static void validate(Film film) throws ValidationException {
        if (film.getName().isBlank()) {
            throw new ValidationException("name: не может быть пустым");
        }

        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина description — 200 символов");
        }

        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            throw new ValidationException("releaseDate: должен быть не раньше 28 декабря 1895 года");
        }

        if (film.getDuration().isNegative()) {
            throw new ValidationException("duration: фильма должна быть положительной");
        }
    }
}
