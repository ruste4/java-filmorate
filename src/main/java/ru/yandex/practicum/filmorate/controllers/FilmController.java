package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable Integer id) {
        if (id == null) {
            throw new IncorrectParameterException("id");
        }

        return filmService.findFilmById(id);
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @PutMapping("{id}/like/{userId}")
    public Film addLikeToFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        if (id == null) {
            throw new IncorrectParameterException("id");
        }

        if (userId == null) {
            throw new IncorrectParameterException("userId");
        }

        return filmService.addLikeToFilm(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public Film deleteLikeToFilm(@PathVariable Integer id, @PathVariable Integer userId) {
        if (id == null) {
            throw new IncorrectParameterException("id");
        }

        if (userId == null) {
            throw new IncorrectParameterException("userId");
        }

        return filmService.deleteLikeToFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(defaultValue = "10", required = false) Integer count
    ) {
        return filmService.getPopularFilms(count);
    }
}
