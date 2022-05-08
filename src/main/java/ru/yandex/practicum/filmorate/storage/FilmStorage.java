package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.Collection;

public interface FilmStorage {
    /**
     * Добавить
     *
     * @param film
     * @return возвращает добавленный фильм
     * @throws FilmAlreadyExistException если фильм ранее был добавлен
     * @throws ValidationException       если поля обекта Film не проходят валидацию
     */
    Film add(Film film) throws FilmAlreadyExistException, ValidationException;

    /**
     * Удалить по id
     *
     * @param id
     * @return возвращает удаленный фильм
     * @throws FilmNotFoundException если фильма с переданным id нет в хранилище
     */
    Film deleteById(int id) throws FilmNotFoundException;

    /**
     * Обновить
     *
     * @param film
     * @return возращает обновленный фильм
     * @throws FilmNotFoundException если переданный фильм не найден в хранилище
     * @throws ValidationException   если переданный фильм не прошел валидацию
     */
    Film update(Film film) throws FilmNotFoundException, ValidationException;

    /**
     * Получить все
     *
     * @return возвращает все фильмы из хранилища
     */
    Collection<Film> getAll();

    /**
     * Найти по id
     *
     * @param id
     * @return возвращает фильм по id
     * @throws FilmNotFoundException если фильма с переданным id нет в хранилище
     */
    Film findById(int id) throws FilmNotFoundException;
}
