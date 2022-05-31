package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.User;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
class FilmControllerTest {
    @Autowired
    private FilmController filmController;
    @Autowired
    private UserController userController;

    @Test
    public void shouldBeExceptionWithNameIsBlank() {

        Film film = new Film();
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.parse("1967-03-25"));
        film.setDuration(Duration.ofSeconds(100));

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    public void shouldBeExceptionWithDescriptionLengthMore200Symbols() {
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
    public void shouldBeExceptionWithReleaseDateBefore28December1895() {
        Film film = new Film();
        film.setName("Звёздный путь");
        film.setDescription("Когда Нерон с планеты Ромул приходит из будущего, чтобы отомстить Федерации...");
        film.setReleaseDate(LocalDate.parse("1895-12-27"));
        film.setDuration(Duration.ofSeconds(100));

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    public void shouldBeExceptionWithDurationIsNegative() {
        Film film = new Film();
        film.setName("Звёздный путь");
        film.setDescription("Когда Нерон с планеты Ромул приходит из будущего, чтобы отомстить Федерации...");
        film.setReleaseDate(LocalDate.parse("1967-03-25"));
        film.setDuration(Duration.ofSeconds(-100));

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    public void shouldBeFilmNotFoundExceptionWithFilmNotAdded() {
        Film film = new Film();
        film.setName("Звёздный путь");
        film.setDescription("Когда Нерон с планеты Ромул приходит из будущего, чтобы отомстить Федерации...");
        film.setReleaseDate(LocalDate.parse("1967-03-25"));
        film.setDuration(Duration.ofSeconds(100));

        assertThrows(FilmNotFoundException.class, () -> filmController.updateFilm(film));
    }

    @Test
    public void shouldBeFilmAlreadyExistExceptionWithIsAddedSecondTime() {
        Film film = new Film();
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
        film.setName("Гарри Поттер 20 лет спустя: Возвращение в Хогвартс");
        film.setDescription("Актеры великой франшизы встречаются в школе магии. Архивные кадры, ...");
        film.setReleaseDate(LocalDate.parse("2022-01-01"));
        film.setDuration(Duration.ofMinutes(99));

        User user = User.builder()
                .email("AmadeusTverskoy462@mail.ru")
                .birthday(LocalDate.parse("1992-06-12"))
                .login("AmadeusTverskoy462")
                .build();

        int filmId = filmController.addFilm(film).getId();
        int userId = userController.addUser(user).getId();

        assertTrue(filmController.addLikeToFilm(filmId, userId).getLikes().contains(userId));
    }

    @Test
    public void shouldBeIncorrectParameterExceptionWithLackUserIdParameterByAddLikeToFilm() {
        assertThrows(IncorrectParameterException.class, () -> {
            filmController.addLikeToFilm(1, null);
        });
    }

    @Test
    public void shouldBeIncorrectParameterExceptionWithLackFilmIdParameterByAddLikeToFilm() {
        assertThrows(IncorrectParameterException.class, () -> {
            filmController.addLikeToFilm(null, 1);
        });
    }

    @Test
    public void shouldBeUserNotFoundExceptionWhenUserNotFundByAddLikeToFilm() {
        assertThrows(UserNotFoundException.class, () -> {
            filmController.addLikeToFilm(1, Integer.MAX_VALUE);
        });
    }

    @Test
    public void shouldBeDeleteLikeToFilm()
            throws ValidationException, FilmAlreadyExistException, UserAlreadyExistException,
            FilmNotFoundException, UserNotFoundException {
        Film film = new Film();
        film.setName("Бука. Мое любимое чудище");
        film.setDescription("Скандал в царском семействе: своенравная принцесса Варвара сбежала из дворца и...");
        film.setReleaseDate(LocalDate.parse("2021-01-01"));
        film.setDuration(Duration.ofMinutes(99));

        User user = User.builder()
                .email("VeraDmitrieva330@mail.ru")
                .birthday(LocalDate.parse("1992-06-12"))
                .login("VeraDmitrieva330")
                .build();

        int filmId = filmController.addFilm(film).getId();
        int userId = userController.addUser(user).getId();

        filmController.addLikeToFilm(filmId, userId);

        assertFalse(filmController.deleteLikeToFilm(filmId, userId).getLikes().contains(userId));
    }

    @Test
    public void shouldBeIncorrectParameterExceptionWithLackFilmIdParameterByDeleteLikeToFilm() {
        assertThrows(IncorrectParameterException.class, () -> {
            filmController.addLikeToFilm(null, 1);
        });
    }

    @Test
    public void shouldBeIncorrectParameterExceptionWithLackUserIdParameterByDeleteLikeToFilm() {
        assertThrows(IncorrectParameterException.class, () -> {
            filmController.addLikeToFilm(1, null);
        });
    }

    @Test
    public void shouldBeGet2FilmsWithCountParameter2() throws ValidationException, FilmAlreadyExistException {
        Film film1 = new Film();
        film1.setName("Film1");
        film1.setDescription("Description");
        film1.setReleaseDate(LocalDate.parse("2021-01-01"));
        film1.setDuration(Duration.ofMinutes(99));
        film1.addLike(1);
        filmController.addFilm(film1);

        Film film2 = new Film();
        film2.setName("Film2");
        film2.setDescription("Description");
        film2.setReleaseDate(LocalDate.parse("2021-01-02"));
        film2.setDuration(Duration.ofMinutes(99));
        film2.addLike(2);
        film2.addLike(1);
        filmController.addFilm(film2);

        Film film3 = new Film();
        film3.setName("Film3");
        film3.setDescription("Description");
        film3.setReleaseDate(LocalDate.parse("2021-01-03"));
        film3.setDuration(Duration.ofMinutes(99));
        film3.addLike(1);
        film3.addLike(2);
        film3.addLike(3);
        filmController.addFilm(film3);

        List<Film> checklist = List.of(film3, film2);

        assertEquals(filmController.getPopularFilms(2), checklist);
    }
}