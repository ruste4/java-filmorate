package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class UserService {
    private final UserStorage storage;

    @Autowired
    public UserService(UserStorage storage) {
        this.storage = storage;
    }

    public User addUser(User user) throws UserAlreadyExistException, ValidationException {
        storage.add(user);
        log.info("Добавить " + user);

        return user;
    }

    public User deleteUser(int id) throws UserNotFoundException {
        User user = storage.deleteById(id);
        log.info("Удалить " + user);

        return user;
    }

    public User updateUser(User user) throws UserNotFoundException, ValidationException {
        storage.update(user);
        log.info("Обновить User.id:" + user.getId() + " на " + user);

        return user;
    }

    public List<User> getAllUsers() {
        log.info("Получить всех пользователей");

        return storage.getAll();
    }

    public User findUserById(int id) throws UserNotFoundException {
        User user = storage.findById(id);
        log.info("Найти User по id:" + id);

        return user;
    }

    public List<User> getAllUsersFriendsById(int id) throws UserNotFoundException {
        User user = storage.findById(id);
        Set<Integer> allFriendsId = user.getAllFriendsId();
        List<User> friends = new ArrayList<>();

        for (int friendId : allFriendsId) {
            User friend = storage.findById(friendId);
            friends.add(friend);
        }
        log.info("Получить всех друзей User.id:" + id);

        return friends;
    }

    public void addNewFriendToTheUser(int userId, int friendId) throws UserNotFoundException {
        User user = storage.findById(userId);
        User friend = storage.findById(friendId);

        user.addNewFriend(friendId);
        friend.addNewFriend(userId);

        log.info("Добавить  User.id:" + userId + " в друзья User.id:" + friendId);
    }

    public void deleteFriendToTheUser(int userId, int friendId) throws UserNotFoundException {
        User user = storage.findById(userId);
        User friend = storage.findById(friendId);

        user.deleteFriend(friendId);
        friend.deleteFriend(userId);

        log.info("Удалить User.id:" + friendId + " из списка друзей User.id:" + userId);
    }

    public Set<User> getCommonFriends(int userId, int friendId) throws UserNotFoundException {
        User user = storage.findById(userId);
        User friend = storage.findById(userId);
        Set<User> commonFriends = new HashSet<>();

        for (int userFriendId : user.getAllFriendsId()) {
            if (friend.getAllFriendsId().contains(userFriendId)) {
                User commonFriend = storage.findById(userFriendId);
                commonFriends.add(commonFriend);
            }
        }
        log.info("Получить общих друзей User.id:" + userId + " c User.id:" + friendId);

        return commonFriends;
    }
}
