package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
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
    public User add(User user) throws UserAlreadyExistException, ValidationException {
        UserValidator.validate(user);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

        Map<String, Object> values = new HashMap<>();
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("email", user.getEmail());
        values.put("birthday", user.getBirthday());

        int user_id = simpleJdbcInsert.executeAndReturnKey(values).intValue();

        user.setId(user_id);

        return user;
    }

    @Override
    public User deleteById(int id) throws UserNotFoundException {
        User user = findById(id);

        String sqlQuery = "DELETE FROM users\n" +
                "WHERE user_id = ?";

        jdbcTemplate.update(sqlQuery, id);

        return user;
    }

    @Override
    public User update(User user) throws UserNotFoundException, ValidationException {
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

        return user;
    }

    @Override
    public Collection<User> getAll() {
        String sqlQuery = "SELECT *\n" +
                "FROM users;";

        return jdbcTemplate.query(sqlQuery, this::mapRowToUser);
    }

    @Override
    public User findById(int id) throws UserNotFoundException {
        String sqlQuery = "SELECT *\n" +
                "FROM users\n" +
                "WHERE user_id = ?;";

        try {
            User user = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUser, id);

            return user;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new UserNotFoundException("User with:" + id + " not found");
            //TODO  Не уверен в этом подходе, на сколько правильно это решение.
            // стоит ли выбрасывает одно исключение при обработке другого, или нужно
            // в ErrorHandler контроллере отдельно обрабатывать?
        }

    }

    @Override
    public User addFriend(int userId, int friendId) {
        User user = findById(userId);
        findById(friendId); // throw UserNotFoundException if user does not exist
        String sqlQuery = "INSERT INTO friendship (user_id, friend_id)\n" +
                "VALUES\n" +
                "(?, ?);";

        jdbcTemplate.update(sqlQuery, userId, friendId);

        return user;
    }

    @Override
    public User deleteFriendToTheUser(int userId, int friendId) {
        User user = findById(userId);

        String sqlQuery = "DELETE FROM friendship\n" +
                "WHERE user_id = ?\n" +
                "     AND friend_id = ?;";

        int deleteResult = jdbcTemplate.update(sqlQuery, userId, friendId);

        if (deleteResult == 0) {
            throw new UserNotFoundException("User with:" + friendId + " not found");
        }

        return user;
    }

    @Override
    public Set<User> getCommonFriends(int userId, int friendId) {
        findById(userId);   // throw UserNotFoundException if user does not exist
        findById(friendId);

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
