package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.models.User;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/users")
public class UserController {
    HashSet<User> users = new HashSet<>();

    @GetMapping
    public Set<User> findAll() {
        return users;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) throws UserNotFoundException {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        throw new UserNotFoundException("Пользователь с id:" + id + " не найден");
    }

    @PostMapping
    public User addUser(@RequestBody User user) throws UserAlreadyExistException {
        if (users.contains(user)) {
            throw new UserAlreadyExistException("Пользователь с id:" + user.getId() + " добавлен ранее");
        }
        users.add(user);

        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) throws UserNotFoundException {
        if (!users.contains(user)) {
            throw new UserNotFoundException("Пользователь с id:" + user.getId() + " не найден");
        }

        users.remove(user);
        users.add(user);

        return user;
    }
}

//todo проверить на соответсвие принципам REST