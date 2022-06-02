package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    @Autowired
    public FilmService(
            @Qualifier("filmDBStorage") FilmStorage filmStorage,
            @Qualifier("userDBStorage") UserStorage userStorage
    ) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) throws ValidationException, FilmAlreadyExistException {
        log.info("Add {}", film);
        Film newFilm = filmStorage.add(film);

        return newFilm;
    }

    public Film deleteFilm(int id) throws FilmNotFoundException {
        log.info("Delete film with id:{}", id);

        return filmStorage.deleteById(id);
    }

    public Film updateFilm(Film film) throws FilmNotFoundException, ValidationException {
        log.info("Update Film.id:{} on {}", film.getId(), film);

        return filmStorage.update(film);
    }

    public Collection<Film> getAllFilms() {
        log.info("Get all films");

        return filmStorage.getAll();
    }

    public Film findFilmById(int id) throws FilmNotFoundException {
        log.info("Find film with id:{}", id);

        return filmStorage.findById(id);
    }

    public Film addLikeToFilm(int filmId, int userId) throws FilmNotFoundException {
        log.info("User.id:{} add like to Film.id:{}", userId, filmId);

        return filmStorage.addLikeToFilm(filmId, userId);
    }

    public Film deleteLikeToFilm(int filmId, int userId) throws FilmNotFoundException {
        log.info("User.id:{} delete like to Film.id:{}", userId, filmId);

        return filmStorage.deleteLikeToFilm(filmId, userId);
    }

    public List<User> getAllLikes(int filmId) throws FilmNotFoundException, UserNotFoundException {
        log.info("Get all likes of Film.id:{}", filmId);
        Set<Integer> likes = filmStorage.getAllLikes(filmId);

        return likes.stream().map(userStorage::findById).collect(Collectors.toList());
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Get popular films");

        return filmStorage.getPopularFilms(count);
    }

}
