package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.FilmMPA;
import ru.yandex.practicum.filmorate.models.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTest {
    @Autowired
    private FilmController filmController;
    @Autowired
    private UserController userController;

    @Test
    public void shouldBeExceptionWithNameIsBlank() {

        Film film = Film.builder()

                .description("Description")
                .releaseDate(LocalDate.parse("1967-03-25"))
                .duration(100)
                .build();

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    public void shouldBeExceptionWithDescriptionLengthMore200Symbols() {
        Film film = Film.builder()
                .name("Звёздный путь")
                .description("Равным образом рамки и место обучения кадров влечет за собой процесс внедрения и " +
                        "модернизации системы обучения кадров, соответствует насущным потребностям. Товарищи!" +
                        " сложившаяся структура организации представляет собой интересный эксперимент проверки " +
                        "направлений прогрессивного развития.")
                .releaseDate(LocalDate.parse("1967-03-25"))
                .duration(100)
                .build();

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    public void shouldBeExceptionWithReleaseDateBefore28December1895() {
        Film film = Film.builder()
                .name("Звёздный путь")
                .description("Когда Нерон с планеты Ромул приходит из будущего, чтобы отомстить Федерации...")
                .releaseDate(LocalDate.parse("1895-12-27"))
                .duration(100)
                .build();

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    public void shouldBeExceptionWithDurationIsNegative() {
        Film film = Film.builder()
                .name("Звёздный путь")
                .description("Когда Нерон с планеты Ромул приходит из будущего, чтобы отомстить Федерации...")
                .releaseDate(LocalDate.parse("1967-03-25"))
                .duration(-100)
                .build();

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    public void shouldBeFilmNotFoundExceptionWithFilmNotAdded() {
        Film film = Film.builder()
                .id(-1)
                .mpa(FilmMPA.builder().id(1).name("G").build())
                .name("Звёздный путь")
                .description("Когда Нерон с планеты Ромул приходит из будущего, чтобы отомстить Федерации...")
                .releaseDate(LocalDate.parse("1967-03-25"))
                .duration(100)
                .build();

        assertThrows(FilmNotFoundException.class, () -> filmController.updateFilm(film));
    }


    @Test
    public void shouldBeAddLikeToFilm()
            throws ValidationException, FilmAlreadyExistException, UserAlreadyExistException, FilmNotFoundException,
            UserNotFoundException {

        Film film = Film.builder()
                .name("Гарри Поттер 20 лет спустя: Возвращение в Хогвартс")
                .description("Актеры великой франшизы встречаются в школе магии. Архивные кадры, ...")
                .releaseDate(LocalDate.parse("2022-01-01"))
                .duration(99)
                .mpa(FilmMPA.builder().id(1).name("G").build())
                .build();

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
            Film film = Film.builder()
                    .name("Гарри Поттер 20 лет спустя: Возвращение в Хогвартс")
                    .description("Актеры великой франшизы встречаются в школе магии. Архивные кадры, ...")
                    .releaseDate(LocalDate.parse("2022-01-01"))
                    .duration(99)
                    .mpa(FilmMPA.builder().id(1).name("G").build())
                    .build();
            filmController.addFilm(film);
            filmController.addLikeToFilm(film.getId(), Integer.MAX_VALUE);
        });
    }

    @Test
    public void shouldBeDeleteLikeToFilm()
            throws ValidationException, FilmAlreadyExistException, UserAlreadyExistException,
            FilmNotFoundException, UserNotFoundException {
        Film film = Film.builder()
                .mpa(FilmMPA.builder().id(1).name("G").build())
                .name("Бука. Мое любимое чудище")
                .description("Скандал в царском семействе: своенравная принцесса Варвара сбежала из дворца и...")
                .releaseDate(LocalDate.parse("2021-01-01"))
                .duration(99)
                .build();

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
    public void shouldBeGet2FilmsWithCountParameter2() {
        Film film1 = Film.builder()
                .name("Film1")
                .mpa(FilmMPA.builder().id(1).name("G").build())
                .description("Description")
                .releaseDate(LocalDate.parse("2021-01-01"))
                .duration(99)
                .build();

        Film film2 = Film.builder()
                .mpa(FilmMPA.builder().id(1).name("G").build())
                .name("Film2")
                .description("Description")
                .releaseDate(LocalDate.parse("2021-01-02"))
                .duration(99)
                .build();

        Film film3 = Film.builder()
                .mpa(FilmMPA.builder().id(1).name("G").build())
                .name("Film3")
                .description("Description")
                .releaseDate(LocalDate.parse("2021-01-03"))
                .duration(99)
                .build();

        User user1 = User.builder()
                .email("user1@mail.ru").
                login("user1")
                .birthday(LocalDate.parse("1992-06-12"))
                .build();
        User user2 = User.builder()
                .email("user2@mail.ru")
                .login("user2")
                .birthday(LocalDate.parse("1992-06-12"))
                .build();
        User user3 = User.builder().
                email("user3@mail.ru")
                .login("user3")
                .birthday(LocalDate.parse("1992-06-12"))
                .build();

        userController.addUser(user1);
        userController.addUser(user2);
        userController.addUser(user3);

        filmController.addFilm(film1);
        filmController.addFilm(film2);
        filmController.addFilm(film3);

        filmController.addLikeToFilm(film1.getId(), user1.getId());

        filmController.addLikeToFilm(film2.getId(), user1.getId());
        filmController.addLikeToFilm(film2.getId(), user2.getId());

        filmController.addLikeToFilm(film3.getId(), user1.getId());
        filmController.addLikeToFilm(film3.getId(), user2.getId());
        filmController.addLikeToFilm(film3.getId(), user3.getId());

        List<Film> checklist = List.of(film3, film2);
        List<Film> filmControllerResult = filmController.getPopularFilms(2);
        assertEquals(filmControllerResult, checklist);
    }
}