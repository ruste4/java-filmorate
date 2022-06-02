package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;

import java.time.LocalDate;

public class FilmValidator {
    private final static LocalDate MIN_RELEASE_DATE = LocalDate.parse("1895-12-28");

    public static void validate(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("name", "не может быть пустым");
        }

        if (film.getDescription().length() > 200) {
            throw new ValidationException("description", "максимальная длина — 200 символов");
        }

        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("releaseDate", "должен быть не раньше 28 декабря 1895 года");
        }

        if (film.getDuration() < 0) {
            throw new ValidationException("duration", "должна быть положительной");
        }
    }
}
