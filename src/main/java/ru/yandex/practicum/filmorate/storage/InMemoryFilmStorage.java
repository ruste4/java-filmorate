package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Map<Integer, Film> FILMS = new HashMap<>();
    private static final AtomicInteger ID_HOLDER = new AtomicInteger();

    @Override
    public Film add(Film film) throws FilmAlreadyExistException, ValidationException {
        if (FILMS.containsValue(film)) {
            throw new FilmAlreadyExistException(
                    "Film " + film.getName() + " " + film.getReleaseDate() + " добавлен ранее"
            );
        }
        film.setId(ID_HOLDER.incrementAndGet());
        FilmValidator.validate(film);
        FILMS.put(film.getId(), film);

        return film;
    }

    @Override
    public Film deleteById(int id) throws FilmNotFoundException {
        Film film = FILMS.remove(id);
        if (film == null) {
            throw new FilmNotFoundException("Фильм с id:" + id + " не найден");
        }

        return film;
    }

    @Override
    public Film update(Film film) throws FilmNotFoundException, ValidationException {
        if (!FILMS.containsKey(film.getId())) {
            throw new FilmNotFoundException("Фильм с id:" + film.getId() + " не найден");
        }
        FilmValidator.validate(film);
        FILMS.put(film.getId(), film);

        return film;
    }

    @Override
    public Collection<Film> getAll() {
        return  FILMS.values();
    }

    @Override
    public Film findById(int id) throws FilmNotFoundException {
        Film film = FILMS.get(id);
        if (film == null) {
            throw new FilmNotFoundException("Фильм с id:" + id + " не найден");
        }

        return film;
    }
}
