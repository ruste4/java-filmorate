package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Map<Integer, Film> FILMS = new HashMap<>();
    private static final AtomicInteger ID_HOLDER = new AtomicInteger();

    @Override
    public Film add(Film film) throws FilmAlreadyExistException, ValidationException {
        if (FILMS.containsValue(film)) {
            throw new FilmAlreadyExistException(
                    "Film " + film.getName() + " " + film.getReleaseDate() + " already exist"
            );
        }
        FilmValidator.validate(film);
        film.setId(ID_HOLDER.incrementAndGet());
        FILMS.put(film.getId(), film);

        return film;
    }

    @Override
    public Film deleteById(int id) throws FilmNotFoundException {
        Film film = FILMS.remove(id);
        if (film == null) {
            throw new FilmNotFoundException("Film with id:" + id + " not found");
        }

        return film;
    }

    @Override
    public Film update(Film film) throws FilmNotFoundException, ValidationException {
        if (!FILMS.containsKey(film.getId())) {
            throw new FilmNotFoundException("Film with id:" + film.getId() + " not found");
        }
        FilmValidator.validate(film);
        FILMS.put(film.getId(), film);

        return film;
    }

    @Override
    public Collection<Film> getAll() {
        return FILMS.values();
    }

    @Override
    public Film findById(int id) throws FilmNotFoundException {
        Film film = FILMS.get(id);
        if (film == null) {
            throw new FilmNotFoundException("Film with id:" + id + " not found");
        }

        return film;
    }

    @Override
    public Film addLikeToFilm(int filmId, int userId) throws FilmNotFoundException {
        Film film = findById(filmId);
        film.addLike(userId);

        return film;
    }

    @Override
    public Film deleteLikeToFilm(int filmId, int userId) throws FilmNotFoundException {
        Film film = findById(filmId);
        film.deleteLike(userId);

        return film;
    }

    @Override
    public Set<Integer> getAllLikes(int filmId) throws FilmNotFoundException, UserNotFoundException {
        Film film = findById(filmId);

        return film.getLikes();
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        List<Film> films = new ArrayList<>(getAll());
        films.sort(Comparator.comparing(Film::getLikeCount).reversed());

        return films.stream().limit(count).collect(Collectors.toList());
    }
}
