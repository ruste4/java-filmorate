package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private HashMap<Integer, Film> films = new HashMap<>();
    private int idCount = 0;

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable int id) throws FilmNotFoundException {
        if (films.containsKey(id)) {
            return films.get(id);
        }
        throw new FilmNotFoundException("Фильм с id:" + id + " не найден");
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) throws FilmAlreadyExistException, ValidationException {
        if (films.containsValue(film)) {
            throw new FilmAlreadyExistException(
                    "Фильм " + film.getName() + " " + film.getReleaseDate() + " добавлен ранее"
            );
        }
        film.setId(++idCount);
        FilmValidator.validate(film);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: " + film);

        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws FilmNotFoundException, ValidationException {
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException("Фильм с id:" + film.getId() + " не найден");
        }
        FilmValidator.validate(film);
        films.put(film.getId(), film);
        log.info("Успешное обновление фильма: " + film);

        return film;
    }
}
