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
    private static final String SQL_FOR_GET_FILM_GENRES_BY_ID = "SELECT g.genre_id FROM film_genre AS fg " +
            "JOIN genre AS g ON fg.genre_id = g.genre_id WHERE fg.film_id = ?";
    private static final String SQL_QUERY_FOR_DELETE_BY_ID = "DELETE FROM films WHERE film_id = ?";
    private static final String SQL_QUERY_FOR_UPDATE = "UPDATE films SET name = ?, mpa_id = ?, release_date  = ?, " +
            "duration = ?, description = ? WHERE film_id = ?;";
    private static final String SQL_QUERY_FOR_GET_ALL = "SELECT * FROM films;";
    private static final String SQL_QUERY_FOR_FIND_BY_ID = "SELECT * FROM films WHERE film_id = ?;";
    private static final String SQL_QUERY_FOR_ADD_LIKE_TO_FILM = "INSERT INTO likes VALUES (?, ?);";
    private static final String SQL_QUERY_FOR_DELETE_LIKE_TO_FILM = "DELETE FROM likes WHERE film_id = ? AND " +
            "user_id = ?;";
    private static final String SQL_QUERY_FOR_GET_POPULAR_FILMS = "SELECT COUNT(l.film_id) AS film_count, f.film_id, " +
            "f.name, f.mpa_id, f.release_date, f.duration, f.description FROM likes AS l RIGHT JOIN films AS f ON " +
            "l.film_id = f.film_id GROUP BY f.film_id ORDER BY film_count DESC LIMIT ?;";
    private static final String SQL_QUERY_FOR_GET_FILM_MPA_BY_ID = "SELECT * FROM mpa WHERE mpa_id = ?;";
    private static final String SQL_QUERY_FOR_GET_FILM_LIKES_BY_ID = "SELECT user_id FROM likes WHERE film_id = ?;";

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
        int deleteResult = jdbcTemplate.update(SQL_QUERY_FOR_DELETE_BY_ID, id);

        if (deleteResult == 0) {
            throw new FilmNotFoundException("Film with id:" + id + " not found");
        }
        log.info("Film with id {} deleted", id);

        return findById(id);
    }

    @Override
    public Film update(Film film) {
        FilmValidator.validate(film);

        int updatedResul = jdbcTemplate.update(
                SQL_QUERY_FOR_UPDATE,
                film.getName(),
                film.getMpa().getId(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getDescription(),
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
        return jdbcTemplate.query(SQL_QUERY_FOR_GET_ALL, this::mapRowToFilm);
    }

    @Override
    public Film findById(int id) {
        try {
            Film film = jdbcTemplate.queryForObject(SQL_QUERY_FOR_FIND_BY_ID, this::mapRowToFilm, id);
            log.info("Film with id:{} found", id);

            return film;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new FilmNotFoundException("Film with id:" + id + " not found");
        }
    }

    @Override
    public Film addLikeToFilm(int filmId, int userId) {
        try {
            jdbcTemplate.update(SQL_QUERY_FOR_ADD_LIKE_TO_FILM, filmId, userId);
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
        int queryResult = jdbcTemplate.update(SQL_QUERY_FOR_DELETE_LIKE_TO_FILM, filmId, userId);

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
        return jdbcTemplate.query(SQL_QUERY_FOR_GET_POPULAR_FILMS, this::mapRowToFilm, count);
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        int filmId = rs.getInt("film_id");
        return Film.builder()
                .id(filmId)
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(LocalDate.parse(rs.getString("release_date")))
                .mpa(getFilmMPAById(rs.getInt("mpa_id")))
                .duration(rs.getInt("duration"))
                .likes(getFilmLikesById(filmId))
                .genre(getFilmGenresById(filmId))
                .build();
    }

    private FilmMPA getFilmMPAById(int id) {
        return jdbcTemplate.queryForObject(SQL_QUERY_FOR_GET_FILM_MPA_BY_ID, this::mapRowToFilmMPA, id);
    }

    private FilmMPA mapRowToFilmMPA(ResultSet rs, int rowNum) throws SQLException {
        return FilmMPA.builder()
                .id(rs.getInt("mpa_id"))
                .name(rs.getString("mpa_name"))
                .build();
    }

    private Set<Integer> getFilmGenresById(int id) {
        List<Integer> queryResult = jdbcTemplate.query(
                SQL_FOR_GET_FILM_GENRES_BY_ID,
                (ResultSet rs, int rowNum) -> rs.getInt("genre_id"),
                id);

        return Set.copyOf(queryResult);
    }

    private Set<Integer> getFilmLikesById(int id) {
        List<Integer> queryResult = jdbcTemplate.query(
                SQL_QUERY_FOR_GET_FILM_LIKES_BY_ID,
                (ResultSet rs, int rowNum) -> rs.getInt("user_id"),
                id);

        return Set.copyOf(queryResult);
    }
}
