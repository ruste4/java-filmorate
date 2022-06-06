package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.models.User;
import ru.yandex.practicum.filmorate.validators.UserValidator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component("userDBStorage")
public class UserDBStorage implements UserStorage {
    private static final String SQL_QUERY_FOR_DELETE_BY_ID = "DELETE FROM users WHERE user_id = ?";
    private static final String SQL_QUERY_FOR_UPDATE = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ?" +
            "WHERE user_id = ?;";
    private static final String SQL_QUERY_FOR_GET_ALL = "SELECT * FROM users;";
    private static final String SQL_QUERY_FOR_FIND_BY_ID = "SELECT * FROM users WHERE user_id = ?;";
    private static final String SQL_QUERY_FOR_ADD_FRIEND = "INSERT INTO friendship (user_id, friend_id) VALUES (?, ?);";
    private static final String SQL_QUERY_FOR_CONFIRM_FRIENDSHIP = "UPDATE friendship SET accept = TRUE " +
            "WHERE user_id = ? AND friend_id = ?;";
    private static final String SQL_QUERY_FOR_DELETE_FRIEND_TO_THE_USER = "DELETE FROM friendship " +
            "WHERE user_id = ? AND friend_id = ?;";
    private static final String SQL_QUERY_GET_COMMON_FRIENDS = "SELECT * FROM (SELECT u.user_id, u.email, u.login, " +
            "u.name, u.birthday FROM friendship AS f INNER JOIN users AS u ON u.user_id = f.friend_id " +
            "WHERE f.user_id = ? UNION SELECT u.user_id, u.email, u.login, u.name, u.birthday FROM friendship AS f " +
            "INNER JOIN users AS u ON u.user_id = f.user_id WHERE f.friend_id = ? AND f.accept = true) AS t1 " +
            "INTERSECT SELECT * FROM (SELECT u.user_id, u.email, u.login, u.name, u.birthday FROM friendship AS f " +
            "INNER JOIN users AS u ON u.user_id = f.friend_id WHERE f.user_id = ? " +
            "UNION SELECT u.user_id, u.email, u.login, u.name, u.birthday FROM friendship AS f " +
            "INNER JOIN users AS u ON u.user_id = f.user_id WHERE f.friend_id = ? AND f.accept = true) AS t2";
    private static final String SQL_QUERY_FOR_GET_ALL_USERS_FRIENDS_BY_ID = "SELECT u.user_id, u.email, u.login, " +
            "u.name,u.birthday FROM friendship AS f INNER JOIN users AS u ON u.user_id = f.friend_id " +
            "WHERE f.user_id = ?  UNION SELECT u.user_id, u.email, u.login, u.name, u.birthday FROM friendship AS f " +
            "INNER JOIN users AS u ON u.user_id = f.user_id WHERE f.friend_id = ? AND f.accept = true;";
    private static final String SQL_QUERY_FOR_GET_USERS_FRIENDS_IDS = "SELECT u.user_id FROM friendship AS f " +
            "INNER JOIN users AS u ON u.user_id = f.friend_id WHERE f.user_id = ? AND f.accept = true " +
            "UNION SELECT u.user_id FROM friendship AS f INNER JOIN users AS u ON u.user_id = f.user_id " +
            "WHERE f.friend_id = ? AND f.accept = true;";

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User add(User user) {
        UserValidator.validate(user);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

        Map<String, Object> values = new HashMap<>();
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("birthday", user.getBirthday());

        try {
            int user_id = simpleJdbcInsert.executeAndReturnKey(values).intValue();
            user.setId(user_id);
            log.info("User id:{} added", user.getId());

            return user;
        } catch (DuplicateKeyException e) {
            if (e.toString().contains("USERS(EMAIL)")) {
                throw new UserAlreadyExistException("User with email:" + user.getEmail() + " already exist");
            } else if (e.toString().contains("USERS(LOGIN)")) {
                throw new UserAlreadyExistException("User with login:" + user.getLogin() + " already exist");
            } else {
                throw new UnsupportedOperationException(e.getMessage());
            }
        }
    }

    @Override
    public User deleteById(int id) {
        User user = findById(id);
        int deleteResult = jdbcTemplate.update(SQL_QUERY_FOR_DELETE_BY_ID, id);
        if (deleteResult == 0) {
            throw new UserNotFoundException("User with:" + id + " not found");
        }
        log.info("User with id:{} deleted", id);
        return user;
    }

    @Override
    public User update(User user) {
        UserValidator.validate(user);
        int updateResult = jdbcTemplate.update(
                SQL_QUERY_FOR_UPDATE,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );

        if (updateResult == 0) {
            throw new UserNotFoundException("User with:" + user.getId() + " not found");
        }
        log.info("User with id:{} updated", user.getId());

        return user;
    }

    @Override
    public Collection<User> getAll() {
        return jdbcTemplate.query(SQL_QUERY_FOR_GET_ALL, this::mapRowToUser);
    }

    @Override
    public User findById(int id) {
        try {
            User user = jdbcTemplate.queryForObject(SQL_QUERY_FOR_FIND_BY_ID, this::mapRowToUser, id);
            log.info("User with id:{} found", id);

            return user;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new UserNotFoundException("User with id:" + id + " not found");
        }

    }

    @Override
    public User addFriend(int userId, int friendId) {
        try {
            jdbcTemplate.update(SQL_QUERY_FOR_ADD_FRIEND, userId, friendId);

            return findById(userId);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("FK_FRIENDSHIP_USER_ID")) {
                throw new UserNotFoundException("User with id:" + userId + " not found");
            } else if (e.getMessage().contains("FK_FRIENDSHIP_FRIEND_ID")) {
                throw new UserNotFoundException("User with id:" + friendId + " not found");
            } else {
                throw new UnsupportedOperationException(e.getMessage());
            }
        }

    }

    @Override
    public User confirmFriendship(int userId, int requestingUser) {
        jdbcTemplate.update(SQL_QUERY_FOR_CONFIRM_FRIENDSHIP, requestingUser, userId);

        return findById(userId);
    }

    @Override
    public User deleteFriendToTheUser(int userId, int friendId) {
        jdbcTemplate.update(SQL_QUERY_FOR_DELETE_FRIEND_TO_THE_USER, userId, friendId);

        return findById(userId);
    }

    @Override
    public Set<User> getCommonFriends(int userId, int friendId) {
        List<User> queryResult = jdbcTemplate.query(SQL_QUERY_GET_COMMON_FRIENDS, this::mapRowToUser, userId, userId, friendId, friendId);

        return Set.copyOf(queryResult);
    }

    @Override
    public List<User> getAllUsersFriendsById(int id) {
        return jdbcTemplate.query(SQL_QUERY_FOR_GET_ALL_USERS_FRIENDS_BY_ID, this::mapRowToUser, id, id);
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        return User.builder()
                .id(rs.getInt("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(LocalDate.parse(rs.getString("birthday")))
                .friendsId(Set.copyOf(getUserFriendsIds(rs.getInt("user_id"))))
                .build();
    }

    private Collection<Integer> getUserFriendsIds(int id) {
        return jdbcTemplate.query(SQL_QUERY_FOR_GET_USERS_FRIENDS_IDS, (ResultSet rs, int num) -> rs.getInt("user_id"), id, id);
    }
}
