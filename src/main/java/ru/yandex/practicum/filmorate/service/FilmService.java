package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) throws ValidationException, FilmAlreadyExistException {
        Film newFilm = filmStorage.add(film);
        log.info("Add {}", film);

        return newFilm;
    }

    public Film deleteFilm(int id) throws FilmNotFoundException {
        Film film = filmStorage.deleteById(id);
        log.info("Delete {}", film);

        return film;
    }

    public Film updateFilm(Film film) throws FilmNotFoundException, ValidationException {
        filmStorage.update(film);
        log.info("Update Film.id:{} on {}", film.getId(), film);

        return film;
    }

    public Collection<Film> getAllFilms() {
        log.info("Get all films");

        return filmStorage.getAll();
    }

    public Film findFilmById(int id) throws FilmNotFoundException {
        Film film = filmStorage.findById(id);
        log.info("Find film dy id:{}", id);

        return film;
    }

    /**
     * Добавить лайк фильму
     *
     * @param filmId id фильма
     * @param userId id пользователя
     * @return возвращает фильм с поставленным лайком
     * @throws FilmNotFoundException если фильм не найден
     */
    public Film addLikeToFilm(int filmId, int userId) throws FilmNotFoundException {
        Film film = filmStorage.findById(filmId);
        film.addLike(userId);
        log.info("User.id:{} add like to Film.id:{}", userId, filmId);

        return film;
    }

    /**
     * Удалить лайк у фильма
     *
     * @param filmId id фильма
     * @param userId id пользователя
     * @return возвращает фильм с удаленным лайком
     * @throws FilmNotFoundException если фильм не найден
     */
    public Film deleteLikeToFilm(int filmId, int userId) throws FilmNotFoundException {
        Film film = filmStorage.findById(filmId);
        film.deleteLike(userId);
        log.info("User.id:{} delete like to Film.id:{}", userId, filmId);

        return film;
    }

    /**
     * Получить все лайки фильма
     *
     * @param filmId
     * @return возвращает список пользователей, которые поставили лайк фильму
     * @throws FilmNotFoundException если фильм не найден
     * @throws UserNotFoundException если в списке лайков есть id несуществующего пользователя
     */
    public List<User> getAllLikes(int filmId) throws FilmNotFoundException, UserNotFoundException {
        Film film = filmStorage.findById(filmId);
        List<User> userLikes = new ArrayList<>();

        for (int userId : film.getLikes()) {
            userLikes.add(userStorage.findById(userId));
        }
        log.info("Get all likes of Film.id:{}", filmId);

        return userLikes;
    }

    /**
     * Получить популярные фильмы
     *
     * @param count количетсво фильмов
     * @return возвращает отсортированный по количеству лайков список фильмов
     */
    public List<Film> getPopularFilms(int count) {
        List<Film> films = new ArrayList<>(filmStorage.getAll());
        films.sort(Comparator.comparing(Film::getLikeCount).reversed());
        log.info("Get popular films");

        return films.stream().limit(count).collect(Collectors.toList());
    }

}
