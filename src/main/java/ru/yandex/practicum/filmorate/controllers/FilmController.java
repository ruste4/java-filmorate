package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/films")
public class FilmController {
    private HashSet<Film> films = new HashSet<>();

    @GetMapping
    public Set<Film> findAll() {
        return films;
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable int id) throws FilmNotFoundException {
        for (Film film : films) {
            if (film.getId() == id) {
                return film;
            }
        }
        throw new FilmNotFoundException("Фильм с id:" + id + " не найден");
    };

    @PostMapping
    public Film addFilm(@RequestBody Film film) throws FilmAlreadyExistException, ValidationException {
        if (films.contains(film)) {
            throw new FilmAlreadyExistException("Фильм с id:" + film.getId() + " был добавлен ранее");
        }

        FilmValidator.validate(film);

        films.add(film);

        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) throws FilmNotFoundException, ValidationException {
        if (!films.contains(film)) {
            throw new FilmNotFoundException("Фильм с id:" + film.getId() + " не найден");
        }

        FilmValidator.validate(film);

        films.remove(film);
        films.add(film);

        return film;
    }
}
