package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.*;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private final UserService userService;

    @Autowired
    public FilmController(FilmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    @GetMapping
    public List<Film> findAll() {
        return filmService.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film findFilmById(@PathVariable int id) throws FilmNotFoundException {
        return filmService.findFilmById(id);
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) throws FilmAlreadyExistException, ValidationException {
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) throws FilmNotFoundException, ValidationException {
        return filmService.updateFilm(film);
    }

    @PutMapping("{id}/like/{userId}")
    public Film addLikeToFilm(@PathVariable String id, @PathVariable String userId)
            throws FilmNotFoundException, UserNotFoundException {
        if (id == null) {
            throw new IncorrectParameterException("id");
        }
        if (userId == null) {
            throw new IncorrectParameterException("userId");
        }
        User user = userService.findUserById(Integer.parseInt(userId));

        if (user == null) {
            throw new UserNotFoundException("User " + userId + " not found");
        }

        Film film = filmService.findFilmById(Integer.parseInt(id));
        film.addLike(Integer.parseInt(userId));

        return film;
    }

    @DeleteMapping("{id}/like/{userId}")
    public Film deleteLikeToFilm(@PathVariable String id, @PathVariable String userId) throws FilmNotFoundException {
        if (id == null) {
            throw new IncorrectParameterException("id");
        }
        if (userId == null) {
            throw new IncorrectParameterException("userId");
        }

        Film film = filmService.findFilmById(Integer.parseInt(id));
        film.deleteLike(Integer.parseInt(userId));

        return film;
    }
}
