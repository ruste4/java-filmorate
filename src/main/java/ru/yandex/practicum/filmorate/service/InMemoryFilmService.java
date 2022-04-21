package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class InMemoryFilmService implements FilmService {
    private static final Map<Integer, Film> FILM_REPOSITORY = new HashMap<>();
    private static final AtomicInteger FILM_ID_HOLDER = new AtomicInteger();

    @Override
    public Film addFilm(Film film) throws FilmAlreadyExistException, ValidationException {
        if (FILM_REPOSITORY.containsValue(film)) {
            throw new FilmAlreadyExistException(
                    "Фильм " + film.getName() + " " + film.getReleaseDate() + " добавлен ранее"
            );
        }
        film.setId(FILM_ID_HOLDER.decrementAndGet());
        FilmValidator.validate(film);
        FILM_REPOSITORY.put(film.getId(), film);
        log.info("Добавлен фильм: " + film);

        return film;
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(FILM_REPOSITORY.values());
    }

    @Override
    public Film findById(int id) throws FilmNotFoundException {
        if (FILM_REPOSITORY.containsKey(id)) {
            return FILM_REPOSITORY.get(id);
        }
        throw new FilmNotFoundException("Фильм с id:" + id + " не найден");
    }

    @Override
    public Film updateFilm(Film film) throws FilmNotFoundException, ValidationException {
        if (!FILM_REPOSITORY.containsKey(film.getId())) {
            throw new FilmNotFoundException("Фильм с id:" + film.getId() + " не найден");
        }
        FilmValidator.validate(film);
        FILM_REPOSITORY.put(film.getId(), film);
        log.info("Успешное обновление фильма: " + film);

        return film;
    }
}
