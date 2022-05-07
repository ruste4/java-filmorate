package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.User;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {
    @Autowired
    private FilmController filmController;
    @Autowired
    private UserController userController;

    @Test
    public void shouldBeExceptionUnderNameIsBlank() {

        Film film = new Film();
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.parse("1967-03-25"));
        film.setDuration(Duration.ofSeconds(100));

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
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

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    public void shouldBeExceptionUnderReleaseDateBefore28December1895() {
        Film film = new Film();
        film.setName("Звёздный путь");
        film.setDescription("Когда Нерон с планеты Ромул приходит из будущего, чтобы отомстить Федерации...");
        film.setReleaseDate(LocalDate.parse("1895-12-27"));
        film.setDuration(Duration.ofSeconds(100));

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    public void shouldBeExceptionUnderDurationIsNegative() {
        Film film = new Film();
        film.setName("Звёздный путь");
        film.setDescription("Когда Нерон с планеты Ромул приходит из будущего, чтобы отомстить Федерации...");
        film.setReleaseDate(LocalDate.parse("1967-03-25"));
        film.setDuration(Duration.ofSeconds(-100));

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    public void shouldBeFilmNotFoundExceptionUnderFilmNotAdded() {
        Film film = new Film();
        film.setId(Integer.MAX_VALUE);
        film.setName("Звёздный путь");
        film.setDescription("Когда Нерон с планеты Ромул приходит из будущего, чтобы отомстить Федерации...");
        film.setReleaseDate(LocalDate.parse("1967-03-25"));
        film.setDuration(Duration.ofSeconds(100));

        assertThrows(FilmNotFoundException.class, () -> filmController.updateFilm(film));
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
            filmController.addFilm(film);
            filmController.addFilm(film);
        });
    }

    @Test
    public void shouldBeAddLikeToFilm()
            throws ValidationException, FilmAlreadyExistException, UserAlreadyExistException, FilmNotFoundException,
            UserNotFoundException {

        Film film = new Film();
        film.setId(Integer.MAX_VALUE);
        film.setName("Гарри Поттер 20 лет спустя: Возвращение в Хогвартс");
        film.setDescription("Актеры великой франшизы встречаются в школе магии. Архивные кадры, ...");
        film.setReleaseDate(LocalDate.parse("2022-01-01"));
        film.setDuration(Duration.ofMinutes(99));

        User user = new User();
        user.setEmail("AmadeusTverskoy462@mail.ru");
        user.setBirthday(LocalDate.parse("1992-06-12"));
        user.setLogin("AmadeusTverskoy462");

        int filmId = filmController.addFilm(film).getId();
        int userId = userController.addUser(user).getId();

        assertTrue(filmController.addLikeToFilm(
                String.valueOf(filmId), String.valueOf(userId)).getLikes().contains(userId)
        );
    }

    @Test
    public void shouldBeIncorrectParameterExceptionUnderLackUserIdParameterByAddLikeToFilm() {
        assertThrows(IncorrectParameterException.class, () -> {
            filmController.addLikeToFilm("1", null);
        });
    }

    @Test
    public void shouldBeIncorrectParameterExceptionUnderLackFilmIdParameterByAddLikeToFilm() {
        assertThrows(IncorrectParameterException.class, () -> {
            filmController.addLikeToFilm(null, "1");
        });
    }

    @Test
    public void shouldBeUserNotFoundExceptionWhenUserNotFundByAddLikeToFilm() {
        assertThrows(UserNotFoundException.class, () -> {
            filmController.addLikeToFilm("1", String.valueOf(Integer.MAX_VALUE));
        });
    }

    @Test
    public void shouldBeDeleteLikeToFilm()
            throws ValidationException, FilmAlreadyExistException, UserAlreadyExistException,
            FilmNotFoundException, UserNotFoundException {
        Film film = new Film();
        film.setId(Integer.MAX_VALUE);
        film.setName("Бука. Мое любимое чудище");
        film.setDescription("Скандал в царском семействе: своенравная принцесса Варвара сбежала из дворца и...");
        film.setReleaseDate(LocalDate.parse("2021-01-01"));
        film.setDuration(Duration.ofMinutes(99));

        User user = new User();
        user.setEmail("VeraDmitrieva330@mail.ru");
        user.setBirthday(LocalDate.parse("1992-06-12"));
        user.setLogin("VeraDmitrieva330");

        int filmId = filmController.addFilm(film).getId();
        int userId = userController.addUser(user).getId();

        filmController.addLikeToFilm(String.valueOf(filmId), String.valueOf(userId));

        assertFalse(
                filmController.deleteLikeToFilm(String.valueOf(filmId), String.valueOf(userId))
                        .getLikes()
                        .contains(userId)
        );
    }

    @Test
    public void shouldBeIncorrectParameterExceptionUnderLackFilmIdParameterByDeleteLikeToFilm() {
        assertThrows(IncorrectParameterException.class, () -> {
            filmController.addLikeToFilm(null, "1");
        });
    }

    @Test
    public void shouldBeIncorrectParameterExceptionUnderLackUserIdParameterByDeleteLikeToFilm() {
        assertThrows(IncorrectParameterException.class, () -> {
            filmController.addLikeToFilm("1", null);
        });
    }
}