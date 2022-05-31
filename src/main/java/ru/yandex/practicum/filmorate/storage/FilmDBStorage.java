package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component("filmDBStorage")
public class FilmDBStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDBStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film add(Film film) throws FilmAlreadyExistException, ValidationException {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");

        Map<String, Object> values = new HashMap<>();
        values.put("name", film.getName());
        values.put("rating", film.getRate());
        values.put("release_date", film.getReleaseDate());
        values.put("duration", film.getDuration());
        values.put("description", film.getDescription());

        int film_id = simpleJdbcInsert.executeAndReturnKey(values).intValue();
        film.setId(film_id);

        return film;
    }

    @Override
    public Film deleteById(int id) throws FilmNotFoundException {
        return null;
    }

    @Override
    public Film update(Film film) throws FilmNotFoundException, ValidationException {
        return null;
    }

    @Override
    public Collection<Film> getAll() {
        return null;
    }

    @Override
    public Film findById(int id) throws FilmNotFoundException {
        return null;
    }
}
