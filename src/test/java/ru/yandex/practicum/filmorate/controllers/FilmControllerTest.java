package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
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
    public void shouldBeExceptionUnderNameIsBlank() {

        Film film = new Film();
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.parse("1967-03-25"));
        film.setDuration(Duration.ofSeconds(100));

        assertThrows(ValidationException.class, () -> controller.addFilm(film));
    }

    @Test
    public void shouldBeExceptionUnderDescriptionLengthMore200Symbols() {
        Film film = new Film();
        film.setName("Звёздный путь");
        film.setDescription("Когда Нерон с планеты Ромул приходит из будущего, чтобы отомстить Федерации, " +
                "конкуренты Кирк и Спок должны объединиться, чтобы не дать ему разрушить все, что им дорого. " +
                "Во время этого будоражащего путешествия, наполненного эффектными боями, юмором и космическими " +
                "угрозами, новоиспеченные члены команды военного корабля «Энтерпрайз» смело встретятся лицом к лицу " +
                "с невообразимыми опасностями.");
        film.setReleaseDate(LocalDate.parse("1967-03-25"));
        film.setDuration(Duration.ofSeconds(100));

        assertThrows(ValidationException.class, () -> controller.addFilm(film));
    }

    @Test
    public void shouldBeExceptionUnderReleaseDateBefore28December1895() {
        Film film = new Film();
        film.setName("Звёздный путь");
        film.setDescription("Когда Нерон с планеты Ромул приходит из будущего, чтобы отомстить Федерации...");
        film.setReleaseDate(LocalDate.parse("1895-12-27"));
        film.setDuration(Duration.ofSeconds(100));

        assertThrows(ValidationException.class, () -> controller.addFilm(film));
    }

    @Test
    public void shouldBeExceptionUnderDurationIsNegative() {
        Film film = new Film();
        film.setName("Звёздный путь");
        film.setDescription("Когда Нерон с планеты Ромул приходит из будущего, чтобы отомстить Федерации...");
        film.setReleaseDate(LocalDate.parse("1967-03-25"));
        film.setDuration(Duration.ofSeconds(-100));

        assertThrows(ValidationException.class, () -> controller.addFilm(film));
    }

    @Test
    public void shouldBeFilmNotFoundExceptionUnderFilmNotAdded() {
        Film film = new Film();
        film.setId(Integer.MAX_VALUE);
        film.setName("Звёздный путь");
        film.setDescription("Когда Нерон с планеты Ромул приходит из будущего, чтобы отомстить Федерации...");
        film.setReleaseDate(LocalDate.parse("1967-03-25"));
        film.setDuration(Duration.ofSeconds(100));

        assertThrows(FilmNotFoundException.class, () -> controller.updateFilm(film));
    }

    @Test
    public void shouldBeFilmAlreadyExistExceptionUnderIsAddedSecondTime() {
        Film film = new Film();
        film.setId(Integer.MAX_VALUE);
        film.setName("Звёздный крейсер «Галактика»");
        film.setDescription("Чудом уцелев после нападения Сайлонов на колонии Кобола, гражданский колониал...");
        film.setReleaseDate(LocalDate.parse("2004-03-16"));
        film.setDuration(Duration.ofSeconds(100));

        assertThrows(FilmAlreadyExistException.class, () -> {
            controller.addFilm(film);
            controller.addFilm(film);
        });
    }
}