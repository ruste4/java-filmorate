package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class InMemoryUserService implements UserService {
    private static final Map<Integer, User> USER_REPOSITORY = new HashMap<>();
    private static final AtomicInteger USER_ID_HOLDER = new AtomicInteger();

    @Override
    public User addUser(User user) throws UserAlreadyExistException, ValidationException {
        if (USER_REPOSITORY.containsValue(user)) {
            throw new UserAlreadyExistException("Пользователь с email:" + user.getEmail() + " добавлен ранее");
        }
        user.setId(USER_ID_HOLDER.incrementAndGet());
        UserValidator.validate(user);
        USER_REPOSITORY.put(user.getId(), user);
        log.info("Добавлен пользователь: " + user);

        return user;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(USER_REPOSITORY.values());
    }

    @Override
    public User findById(int id) throws UserNotFoundException {
        if (USER_REPOSITORY.containsKey(id)) {
            return USER_REPOSITORY.get(id);
        }
        throw new UserNotFoundException("Пользователь с id:" + id + " не найден");
    }

    @Override
    public User updateUser(User user) throws UserNotFoundException, ValidationException {
        if (!USER_REPOSITORY.containsKey(user.getId())) {
            throw new UserNotFoundException("Пользователь с id:" + user.getId() + " не найден");
        }
        UserValidator.validate(user);
        USER_REPOSITORY.put(user.getId(), user);
        log.info("Успешное обновление пользователя: " + user);

        return user;
    }
}
