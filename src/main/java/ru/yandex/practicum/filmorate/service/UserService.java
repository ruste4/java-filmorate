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

    /**
     * Получить всех друзей пользователя
     *
     * @param id
     * @return возвращает список друзей пользователя
     * @throws UserNotFoundException если пользователь не найден, поиск идет по переданному id
     */
    public List<User> getAllUsersFriendsById(int id) throws UserNotFoundException {
        User user = storage.findById(id);
        Set<Integer> allFriendsId = user.getAllFriendsId();
        List<User> friends = new ArrayList<>();

        for (int friendId : allFriendsId) {
            User friend = storage.findById(friendId);
            friends.add(friend);
        }
        log.info("Get all users friends by id:{}", id);

        return friends;
    }

    /**
     * Добавить нового друга пользователю
     *
     * @param userId   id пользователя
     * @param friendId id друга, которого нужно добавить в друзья пользователю
     * @throws UserNotFoundException если переданные userId или friendId не найдены
     */
    public User addNewFriendToTheUser(int userId, int friendId) throws UserNotFoundException {
        User user = storage.findById(userId);
        User friend = storage.findById(friendId);

        user.addNewFriend(friendId);
        friend.addNewFriend(userId);

        log.info("Add new User.id:{} to the friends list at User.id:{}", friendId, userId);

        return user;
    }

    /**
     * Удалить друга у ползователя
     *
     * @param userId   id пользователя
     * @param friendId id друга, которого нужно удалить из друзей пользователя
     * @throws UserNotFoundException если переданные userId или friendId не найдены
     */
    public User deleteFriendToTheUser(int userId, int friendId) throws UserNotFoundException {
        User user = storage.findById(userId);
        User friend = storage.findById(friendId);

        user.deleteFriend(friendId);
        friend.deleteFriend(userId);

        log.info("Delete User.id:{} from the friends list at User.id:{}", friendId, userId);

        return user;
    }

    /**
     * Получить общих друзей
     *
     * @param userId   id пользователя
     * @param friendId id друга
     * @return возвращает набор общих друзей
     * @throws UserNotFoundException если переданные userId или friendId не найдены
     */
    public Set<User> getCommonFriends(int userId, int friendId) throws UserNotFoundException {
        User user = storage.findById(userId);
        User friend = storage.findById(friendId);
        Set<User> commonFriends = new HashSet<>();

        for (int userFriendId : user.getAllFriendsId()) {
            if (friend.getAllFriendsId().contains(userFriendId)) {
                User commonFriend = storage.findById(userFriendId);
                commonFriends.add(commonFriend);
            }
        }
        log.info("Get common friends User.id:{} and User.id:{}", userId, friendId);

        return commonFriends;
    }
}
