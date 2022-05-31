package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(
            @Qualifier("userDBStorage") UserStorage storage
    ) {
        this.storage = storage;
    }

    public User addUser(User user) throws UserAlreadyExistException, ValidationException {
        User newUser = storage.add(user);
        log.info("Add {}", user);

        return newUser;
    }

    public User deleteUser(int id) throws UserNotFoundException {
        User user = storage.deleteById(id);
        log.info("Delete {}", user);

        return user;
    }

    public User updateUser(User user) throws UserNotFoundException, ValidationException {
        storage.update(user);
        log.info("Update User.id:{} on {}", user.getId(), user);

        return user;
    }

    public Collection<User> getAllUsers() {
        log.info("Get all users");

        return storage.getAll();
    }

    public User findUserById(int id) throws UserNotFoundException {
        User user = storage.findById(id);
        log.info("Find user by id:{}", id);

        return user;
    }

    public List<User> getAllUsersFriendsById(int id) throws UserNotFoundException {
        log.info("Get all users friends by id:{}", id);

        return storage.getAllUsersFriendsById(id);
    }

    public User addNewFriendToTheUser(int userId, int friendId) throws UserNotFoundException {
        log.info("Add new User.id:{} to the friends list at User.id:{}", friendId, userId);

        return storage.addFriend(userId, friendId);
    }

    public User deleteFriendToTheUser(int userId, int friendId) throws UserNotFoundException {
        log.info("Delete User.id:{} from the friends list at User.id:{}", friendId, userId);

        return storage.deleteFriendToTheUser(userId, friendId);
    }

    public Set<User> getCommonFriends(int userId, int friendId) throws UserNotFoundException {
        log.info("Get common friends User.id:{} and User.id:{}", userId, friendId);

        return storage.getCommonFriends(userId, friendId);
    }
}
