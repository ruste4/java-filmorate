package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;

import java.util.List;

public interface UserService {
    /**
     * Добавить пользователя
     *
     * @param user
     * @return возвращает добаленного пользователя
     * @throws UserAlreadyExistException если пользователь был добавлен ранее
     * @throws ValidationException       если поля объекта User не проходят валидацию
     */
    User addUser(User user) throws UserAlreadyExistException, ValidationException;

    /**
     * Найти все
     *
     * @return Возвращает всех пользователей из хранилища
     */
    List<User> findAll();

    /**
     * Найти по id
     *
     * @param id
     * @return возвращает найденного пользователя по id
     * @throws UserNotFoundException если пользователь не был найден
     */
    User findById(int id) throws UserNotFoundException;

    /**
     * Обновить пользователя
     *
     * @param user
     * @return возвращает обновленного пользователя
     * @throws UserNotFoundException если пользователь не найден
     * @throws ValidationException   если поля объекта User не прошли валидацию
     */
    User updateUser(User user) throws UserNotFoundException, ValidationException;
}
