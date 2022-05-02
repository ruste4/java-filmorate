package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;

import java.util.List;

public interface UserStorage {
    /**
     * Добавить
     *
     * @param user
     * @return возвращает добавленного пользователя
     * @throws UserAlreadyExistException если пользователь ранее был добавлен
     * @throws ValidationException       если пользователь не прошел валидацию
     */
    User add(User user) throws UserAlreadyExistException, ValidationException;

    /**
     * Удалить по id
     *
     * @param id
     * @return возвращает удаленного пользователя
     * @throws UserNotFoundException если пользователя с переданным id нет в хранилище
     */
    User deleteById(int id) throws UserNotFoundException;

    /**
     * Обновить
     *
     * @param user
     * @return возвращает обновленного пользователя
     * @throws UserNotFoundException если переданный пользователь не найден в хранилище
     * @throws ValidationException   если пользователь не прошел валидацию
     */
    User update(User user) throws UserNotFoundException, ValidationException;

    /**
     * Получить все
     *
     * @return возвращает всех пользователей из хранилища
     */
    List<User> getAll();

    /**
     * Найти по id
     *
     * @param id
     * @return возвращает пользователя по id
     * @throws UserNotFoundException если пользователя с переданным id нет в хранилище
     */
    User findById(int id) throws UserNotFoundException;
}
