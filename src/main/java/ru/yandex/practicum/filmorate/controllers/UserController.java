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
import java.util.List;
import java.util.Set;

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
    public User getUserById(@PathVariable(required = false) Integer id) throws UserNotFoundException {
        if (id == null) {
            throw new IncorrectParameterException("id");
        }

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
            @PathVariable(required = false) Integer userId,
            @PathVariable(required = false) Integer friendId) throws UserNotFoundException {

        if (userId == null) {
            throw new IncorrectParameterException("userId");
        }

        if (friendId == null) {
            throw new IncorrectParameterException("friendId");
        }

        return userService.addNewFriendToTheUser(userId, friendId);
    }

    @DeleteMapping("{userId}/friends/{friendId}")
    public User deleteToFriends(
            @PathVariable(required = false) Integer userId,
            @PathVariable(required = false) Integer friendId
    ) throws UserNotFoundException {
        if (userId == null) {
            throw new IncorrectParameterException("userId");
        }

        if (friendId == null) {
            throw new IncorrectParameterException("friendId");
        }

        return userService.deleteFriendToTheUser(userId, friendId);
    }

    @GetMapping("{userId}/friends")
    public List<User> getUsersFriends(@PathVariable(required = false) Integer userId) throws UserNotFoundException {
        if (userId == null) {
            throw new IncorrectParameterException("userId");
        }

        return userService.getAllUsersFriendsById(userId);
    }

    @GetMapping("{userId}/friends/common/{friendId}")
    public Set<User> getCommonFriends(
            @PathVariable(required = false) Integer userId,
            @PathVariable(required = false) Integer friendId
    ) throws UserNotFoundException {
        if (userId == null) {
            throw new IncorrectParameterException("userId");
        }

        if (friendId == null) {
            throw new IncorrectParameterException("friendId");
        }

        return userService.getCommonFriends(userId, friendId);
    }
}