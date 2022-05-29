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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(LocalDate.parse(rs.getString("birthday")));

        return user;
    }
}
