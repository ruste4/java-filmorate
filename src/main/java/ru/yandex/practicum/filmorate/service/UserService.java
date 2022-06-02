package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
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

    public User addUser(User user) {
        log.info("Add {}", user);

        return storage.add(user);
    }

    public User deleteUser(int id) {
        log.info("Delete user with id:{}", id);

        return storage.deleteById(id);
    }

    public User updateUser(User user) {
        log.info("Update with id:{} on {}", user.getId(), user);

        return storage.update(user);
    }

    public Collection<User> getAllUsers() {
        log.info("Get all users");

        return storage.getAll();
    }

    public User findUserById(int id) {
        User user = storage.findById(id);
        log.info("Find user by id:{}", id);

        return user;
    }

    public List<User> getAllUsersFriendsById(int id) {
        log.info("Get all users friends by id:{}", id);

        return storage.getAllUsersFriendsById(id);
    }

    public User addNewFriendToTheUser(int userId, int friendId) {
        log.info("Add  User.id:{} to the friends list at User.id:{}", friendId, userId);

        return storage.addFriend(userId, friendId);
    }

    public User deleteFriendToTheUser(int userId, int friendId) {
        log.info("Delete User.id:{} from the friends list at User.id:{}", friendId, userId);

        return storage.deleteFriendToTheUser(userId, friendId);
    }

    public Set<User> getCommonFriends(int userId, int friendId) {
        log.info("Get common friends User.id:{} and User.id:{}", userId, friendId);

        return storage.getCommonFriends(userId, friendId);
    }
}
