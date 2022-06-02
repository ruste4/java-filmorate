package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Map<Integer, User> USERS = new HashMap<>();
    private static final AtomicInteger ID_HOLDER = new AtomicInteger();

    @Override
    public User add(User user) {
        if (USERS.containsValue(user)) {
            throw new UserAlreadyExistException("User with email:" + user.getEmail() + " already exist");
        }
        user.setId(ID_HOLDER.incrementAndGet());
        UserValidator.validate(user);
        USERS.put(user.getId(), user);

        return user;
    }

    @Override
    public User deleteById(int id) {
        User user = USERS.remove(id);
        if (user == null) {
            throw new UserNotFoundException("User with id:" + id + " not found");
        }

        return user;
    }

    @Override
    public User update(User user) {
        if (!USERS.containsKey(user.getId())) {
            throw new UserNotFoundException("User with:" + user.getId() + " not found");
        }
        UserValidator.validate(user);
        USERS.put(user.getId(), user);

        return user;
    }

    @Override
    public Collection<User> getAll() {
        return USERS.values();
    }

    @Override
    public User findById(int id) {
        User user = USERS.get(id);
        if (user == null) {
            throw new UserNotFoundException("User with id:" + id + " not found");
        }

        return user;
    }

    @Override
    public User addFriend(int userId, int friendId) {
        User user = findById(userId);
        User friend = findById(friendId);

        user.addNewFriend(friendId);
        friend.addNewFriend(userId);

        return user;
    }

    @Override
    public User confirmFriendship(int userId, int requestingUser) {
        throw new UnsupportedOperationException("InMemoryUserStorage.confirmFriendship() unsupported");
    }

    @Override
    public User deleteFriendToTheUser(int userId, int friendId) {
        User user = findById(userId);
        User friend = findById(friendId);

        user.deleteFriend(friendId);
        friend.deleteFriend(userId);

        return user;
    }

    @Override
    public Set<User> getCommonFriends(int userId, int friendId) {
        User user = findById(userId);
        User friend = findById(friendId);
        Set<User> commonFriends = new HashSet<>();

        for (int userFriendId : user.getFriendsId()) {
            if (friend.getFriendsId().contains(userFriendId)) {
                User commonFriend = findById(userFriendId);
                commonFriends.add(commonFriend);
            }
        }
        return commonFriends;
    }

    @Override
    public List<User> getAllUsersFriendsById(int id) {
        User user = findById(id);
        Set<Integer> allFriendsId = user.getFriendsId();
        List<User> friends = new ArrayList<>();

        for (int friendId : allFriendsId) {
            User friend = findById(friendId);
            friends.add(friend);
        }

        return friends;
    }

}
