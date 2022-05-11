package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Map<Integer, User> USERS = new HashMap<>();
    private static final AtomicInteger ID_HOLDER = new AtomicInteger();

    @Override
    public User add(User user) throws UserAlreadyExistException, ValidationException {
        if (USERS.containsValue(user)) {
            throw new UserAlreadyExistException("User with email:" + user.getEmail() + " already exist");
        }
        user.setId(ID_HOLDER.incrementAndGet());
        UserValidator.validate(user);
        USERS.put(user.getId(), user);

        return user;
    }

    @Override
    public User deleteById(int id) throws UserNotFoundException {
        User user = USERS.remove(id);
        if (user == null) {
            throw new UserNotFoundException("User with id:" + id + " not found");
        }

        return user;
    }

    @Override
    public User update(User user) throws UserNotFoundException, ValidationException {
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
    public User findById(int id) throws UserNotFoundException {
        User user = USERS.get(id);
        if (user == null) {
            throw new UserNotFoundException("User with id:" + id + " not found");
        }

        return user;
    }
}
