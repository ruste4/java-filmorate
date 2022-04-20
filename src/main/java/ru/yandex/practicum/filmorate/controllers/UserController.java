package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    HashMap<Integer, User> users = new HashMap<>();
    int idCount = 0;
    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) throws UserNotFoundException {
        if (users.containsKey(id)) {
            return users.get(id);
        }
        throw new UserNotFoundException("Пользователь с id:" + id + " не найден");
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) throws UserAlreadyExistException, ValidationException {
        if (users.containsValue(user)) {
            throw new UserAlreadyExistException("Пользователь с email:" + user.getEmail() + " добавлен ранее");
        }
        user.setId(++idCount);
        UserValidator.validate(user);
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: " + user);

        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws UserNotFoundException, ValidationException {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("Пользователь с id:" + user.getId() + " не найден");
        }
        UserValidator.validate(user);
        users.put(user.getId(), user);
        log.info("Успешное обновление пользователя: " + user);

        return user;
    }
}