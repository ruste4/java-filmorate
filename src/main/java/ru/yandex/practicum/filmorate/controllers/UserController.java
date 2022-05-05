package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) throws UserNotFoundException {
        return userService.findUserById(id);
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) throws UserAlreadyExistException, ValidationException {
        return userService.addUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) throws UserNotFoundException, ValidationException {
        return userService.updateUser(user);
    }

    @PutMapping("{userId}/friends/{friendId}")
    public User addToFriends(
            @PathVariable(required = false) String userId,
            @PathVariable(required = false) String friendId) throws UserNotFoundException {

        if (userId == null) {
            throw new IncorrectParameterException("userId");
        }

        if (friendId == null) {
            throw new IncorrectParameterException("friendId");
        }

        return userService.addNewFriendToTheUser(Integer.parseInt(userId), Integer.parseInt(friendId));
    }

    @DeleteMapping("{userId}/friends/{friendId}")
    public User deleteToFriends(
            @PathVariable(required = false) String userId,
            @PathVariable(required = false) String friendId
    ) throws UserNotFoundException {
        if (userId == null) {
            throw new IncorrectParameterException("userId");
        }

        if (friendId == null) {
            throw new IncorrectParameterException("friendId");
        }

        return userService.deleteFriendToTheUser(Integer.parseInt(userId), Integer.parseInt(friendId));
    }
}