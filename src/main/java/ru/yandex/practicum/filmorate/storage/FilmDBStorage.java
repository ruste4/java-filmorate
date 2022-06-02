package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.*;
import ru.yandex.practicum.filmorate.models.Film;
import ru.yandex.practicum.filmorate.models.FilmMPA;
import ru.yandex.practicum.filmorate.validators.FilmValidator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component("filmDBStorage")
public class FilmDBStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film add(Film film) {
        FilmValidator.validate(film);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("mpa_id", film.getMpa().getId());
        values.put("rate", film.getRate());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("description", film.getDescription());

        int film_id = simpleJdbcInsert.executeAndReturnKey(values).intValue();
        film.setId(film_id);

        log.info("Add film with id:{} added", film.getId());

        return film;
    }

    @Override
    public Film deleteById(int id) {
        String sqlQuery = "DELETE FROM films\n" +
                "WHERE film_id = ?";

        int deleteResult = jdbcTemplate.update(sqlQuery, id);

        if (deleteResult == 0) {
            throw new FilmNotFoundException("Film with id:" + id + " not found");
        }
        log.info("Film with id {} deleted", id);

        return findById(id);
    }

    @Override
    public Film update(Film film) {
        FilmValidator.validate(film);

        String sqlQuery = "UPDATE films\n" +
                "SET name = ?,\n" +
                "    mpa_id = ?,\n" +
                "    release_date  = ?,\n" +
                "    duration = ?,\n" +
                "    description = ?,\n" +
                "    rate = ?\n" +
                "WHERE film_id = ?;";

        int updatedResul = jdbcTemplate.update(
                sqlQuery,
                film.getName(),
                film.getMpa().getId(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getDescription(),
                film.getRate(),
                film.getId()
        );

        if (updatedResul == 0) {
            throw new FilmNotFoundException("Film with id:" + film.getId() + " not found");
        }

        log.info("Film with id:{} updated", film.getId());

        return film;
    }

    @Override
    public Collection<Film> getAll() {
        String sqlQuery = "SELECT *\n" +
                "FROM films;";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm);
    }

    @Override
    public Film findById(int id) {
        String sqlQuery = "SELECT *\n" +
                "FROM films\n" +
                "WHERE film_id = ?;";
        try {
            Film film = jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilm, id);
            log.info("Film with id:{} found", id);

            return film;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new FilmNotFoundException("Film with id:" + id + " not found");
        }
    }

    @Override
    public Film addLikeToFilm(int filmId, int userId) {
        String sqlQuery = "INSERT INTO likes\n" +
                "VALUES\n" +
                "(?, ?);\n";

        try {
            jdbcTemplate.update(sqlQuery, filmId, userId);
            return findById(filmId);
        } catch (DataIntegrityViolationException e) {
            String errorMessage = e.getMostSpecificCause().getMessage();
            log.warn(errorMessage);

            if (errorMessage.contains("FK_LIKES_FILM_ID")) {
                throw new FilmNotFoundException("Film with id:" + filmId + " not found");
            } else if (errorMessage.contains("FK_LIKES_USER_ID")) {
                throw new UserNotFoundException("User with id:" + userId + " not found");
            } else {
                throw new LikeAlreadyExistException(
                        "Like by film:id=" + filmId + " from user:id=" + userId + " already exist"
                );
            }
        }

    }

    @Override
    public Film deleteLikeToFilm(int filmId, int userId) {
        String sqlQuery = "DELETE FROM likes\n" +
                "WHERE film_id = ? \n" +
                "AND user_id = ?;\n";

        int queryResult = jdbcTemplate.update(sqlQuery, filmId, userId);

        if (queryResult == 0) {
            throw new LikeNotFoundException("Like by film:id=" + filmId + " from user:id=" + userId + " not found");
        }

        log.info("Like by film:id={} from user:id={} deleted", filmId, userId);
        return findById(filmId);
    }

    @Override
    public Set<Integer> getAllLikes(int filmId) {
        return findById(filmId).getLikes();
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sqlQuery = "SELECT COUNT(l.film_id) AS film_count, \n" +
                "    f.film_id, \n" +
                "    f.name, \n" +
                "    f.rate,  \n" +
                "    f.mpa_id, \n" +
                "    f.release_date, \n" +
                "    f.duration, \n" +
                "    f.description\n" +
                "FROM likes AS l\n" +
                "RIGHT JOIN films AS f ON l.film_id = f.film_id\n" +
                "GROUP BY f.film_id\n" +
                "ORDER BY film_count DESC\n" +
                "LIMIT ?;";

        return jdbcTemplate.query(sqlQuery, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        return Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(LocalDate.parse(rs.getString("release_date")))
                .rate(rs.getInt("rate"))
                .mpa(getFilmMPAById(rs.getInt("mpa_id")))
                .duration(rs.getInt("duration"))
                .likes(getFilmLikesById(rs.getInt("film_id")))
                .build();
    }

    private FilmMPA getFilmMPAById(int id) {
        String sqlQuery = "SELECT *\n" +
                "FROM mpa\n" +
                "WHERE mpa_id = ?;";

        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToFilmMPA, id);
    }

    private FilmMPA mapRowToFilmMPA(ResultSet rs, int rowNum) throws SQLException {
        return FilmMPA.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();
    }

    private Set<Integer> getFilmLikesById(int id) {
        String sqlQuery = "SELECT user_id \n" +
                "FROM likes\n" +
                "WHERE film_id = ?;";

        List<Integer> queryResult = jdbcTemplate.query(
                sqlQuery,
                (ResultSet rs, int rowNum) -> rs.getInt("user_id"),
                id);

        return Set.copyOf(queryResult);
    }
}
