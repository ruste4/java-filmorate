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
        String sqlQuery = "DELETE FROM users\n" +
                "WHERE user_id = ?";

        int deleteResult = jdbcTemplate.update(sqlQuery, id);
        if (deleteResult == 0) {
            throw new UserNotFoundException("User with:" + id + " not found");
        }
        log.info("User with id:{} deleted", id);
        return user;
    }

    @Override
    public User update(User user) {
        UserValidator.validate(user);

        String sqlQuery = "UPDATE users\n" +
                "SET email = ?,\n" +
                "    login = ?,\n" +
                "    name = ?,\n" +
                "    birthday = ?\n" +
                "WHERE user_id = ?;";

        int updateResult = jdbcTemplate.update(
                sqlQuery,
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
        String sqlQuery = "SELECT *\n" +
                "FROM users;";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User findById(int id) {
        String sqlQuery = "SELECT *\n" +
                "FROM users\n" +
                "WHERE user_id = ?;";

        try {
            User user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);
            log.info("User with id:{} found", id);

            return user;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new UserNotFoundException("User with id:" + id + " not found");
            //TODO  Не уверен в этом подходе. На сколько правильно это решение?
            // Стоит ли выбрасывает одно исключение при обработке другого, или нужно
            // в ErrorHandler контроллере отдельно обрабатывать?
        }

    }

    @Override
    public User addFriend(int userId, int friendId) {
        String sqlQuery = "INSERT INTO friendship (user_id, friend_id)\n" +
                "VALUES\n" +
                "(?, ?);";

        try {
            jdbcTemplate.update(sqlQuery, userId, friendId);

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
        String sqlQuery = "UPDATE friendship\n" +
                "SET accept = TRUE\n" +
                "WHERE user_id = ? AND friend_id = ?;\n";

        jdbcTemplate.update(sqlQuery, requestingUser, userId);

        return findById(userId);
    }

    @Override
    public User deleteFriendToTheUser(int userId, int friendId) {
        String sqlQuery = "DELETE FROM friendship\n" +
                "WHERE user_id = ?\n" +
                "     AND friend_id = ?;";

        jdbcTemplate.update(sqlQuery, userId, friendId);

        return findById(userId);
    }

    @Override
    public Set<User> getCommonFriends(int userId, int friendId) {
        String sqlQuery = "SELECT *\n" +
                "FROM (\n" +
                "    SELECT u.user_id,\n" +
                "           u.email,\n" +
                "           u.login,\n" +
                "           u.name,\n" +
                "           u.birthday\n" +
                "    FROM friendship AS f\n" +
                "    INNER JOIN users AS u ON u.user_id = f.friend_id\n" +
                "    WHERE f.user_id = ? \n" +
                "    UNION\n" +
                "    SELECT u.user_id,\n" +
                "           u.email,\n" +
                "           u.login,\n" +
                "           u.name,\n" +
                "           u.birthday\n" +
                "    FROM friendship AS f\n" +
                "    INNER JOIN users AS u ON u.user_id = f.user_id\n" +
                "    WHERE f.friend_id = ? AND f.accept = true\n" +
                ") AS t1\n" +
                "\n" +
                "INTERSECT\n" +
                "\n" +
                "SELECT *\n" +
                "FROM (\n" +
                "    SELECT u.user_id,\n" +
                "           u.email,\n" +
                "           u.login,\n" +
                "           u.name,\n" +
                "           u.birthday\n" +
                "    FROM friendship AS f\n" +
                "    INNER JOIN users AS u ON u.user_id = f.friend_id\n" +
                "    WHERE f.user_id = ? \n" +
                "    UNION\n" +
                "    SELECT u.user_id,\n" +
                "           u.email,\n" +
                "           u.login,\n" +
                "           u.name,\n" +
                "           u.birthday\n" +
                "    FROM friendship AS f\n" +
                "    INNER JOIN users AS u ON u.user_id = f.user_id\n" +
                "    WHERE f.friend_id = ? AND f.accept = true\n" +
                ") AS t2";

        List<User> queryResult = jdbcTemplate.query(sqlQuery, this::mapRowToUser, userId, userId, friendId, friendId);

        return Set.copyOf(queryResult);
    }

    @Override
    public List<User> getAllUsersFriendsById(int id) {
        String sqlQuery = "SELECT u.user_id,\n" +
                "       u.email,\n" +
                "       u.login,\n" +
                "       u.name,\n" +
                "       u.birthday\n" +
                "FROM friendship AS f\n" +
                "INNER JOIN users AS u ON u.user_id = f.friend_id\n" +
                "WHERE f.user_id = ? \n" +
                "UNION\n" +
                "SELECT u.user_id,\n" +
                "       u.email,\n" +
                "       u.login,\n" +
                "       u.name,\n" +
                "       u.birthday\n" +
                "FROM friendship AS f\n" +
                "INNER JOIN users AS u ON u.user_id = f.user_id\n" +
                "WHERE f.friend_id = ? AND f.accept = true;";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser, id, id);
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
        String sqlQuery = "SELECT u.user_id\n" +
                "FROM friendship AS f\n" +
                "INNER JOIN users AS u ON u.user_id = f.friend_id\n" +
                "WHERE f.user_id = ? AND f.accept = true\n" +
                "UNION\n" +
                "SELECT u.user_id\n" +
                "FROM friendship AS f\n" +
                "INNER JOIN users AS u ON u.user_id = f.user_id\n" +
                "WHERE f.friend_id = ? AND f.accept = true;";

        return jdbcTemplate.query(sqlQuery, (ResultSet rs, int num) -> rs.getInt("user_id"), id, id);
    }
}
