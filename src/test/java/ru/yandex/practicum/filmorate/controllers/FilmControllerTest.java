package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {
    @Autowired
    private FilmController controller;

    @Test
    public void validationNameIsBlank() {

        Film film = new Film();
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.parse("1967-03-25"));
        film.setDuration(Duration.ofSeconds(100));

        assertThrows(ValidationException.class, () -> {
            controller.addFilm(film);
        });
    }

    @Test
    public void maxLengthDescription200Symbols() {

    }

}