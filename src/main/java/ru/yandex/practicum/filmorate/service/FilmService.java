package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.List;

public interface FilmService {
    /**
     * Добавить фильм фильм
     *
     * @param film
     * @return Возвращает добавленный фильм
     * @throws FilmAlreadyExistException если фильм ранее был добавлен
     * @throws ValidationException       если поля обекта Film не проходят валидацию
     */
    Film addFilm(Film film) throws FilmAlreadyExistException, ValidationException;

    /**
     * Найти все
     *
     * @return Возвращает все записанные фильмы в хранилище
     */
    List<Film> findAll();

    /**
     * Найти по id
     *
     * @param id
     * @return Возвращает найденный фильм по id
     * @throws FilmNotFoundException если фильм не добалвен в хранилище
     */
    Film findById(int id) throws FilmNotFoundException;

    /**
     * Обновить
     *
     * @param film
     * @return Возвращает обновленный фильм
     * @throws FilmNotFoundException если фильм не добалвен в хранилище
     * @throws ValidationException   если поля обекта Film не проходят валидацию
     */
    Film update(Film film) throws FilmNotFoundException, ValidationException;
}