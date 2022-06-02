package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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

    /**
     * Добавить лайк фильму
     *
     * @param filmId id фильма
     * @param userId id пользователя
     * @return возвращает фильм с поставленным лайком
     * @throws FilmNotFoundException если фильм не найден
     */
    Film addLikeToFilm(int filmId, int userId) throws FilmNotFoundException;

    /**
     * Удалить лайк у фильма
     *
     * @param filmId id фильма
     * @param userId id пользователя
     * @return возвращает фильм с удаленным лайком
     * @throws FilmNotFoundException если фильм не найден
     */
    Film deleteLikeToFilm(int filmId, int userId) throws FilmNotFoundException;

    /**
     * Получить все лайки фильма
     *
     * @param filmId
     * @return возвращает список id пользователей, которые поставили лайк фильму
     * @throws FilmNotFoundException если фильм не найден
     * @throws UserNotFoundException если в списке лайков есть id несуществующего пользователя
     */
    Set<Integer> getAllLikes(int filmId) throws FilmNotFoundException;

    /**
     * Получить популярные фильмы
     *
     * @param count количетсво фильмов
     * @return возвращает отсортированный по количеству лайков список фильмов
     */
    List<Film> getPopularFilms(int count);
}
