package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Repository
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAll() {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM genres;");
        List<Genre> genres = new ArrayList<>();
        while (sqlRowSet.next()) {
            Genre genre = new Genre(sqlRowSet.getInt("id"), sqlRowSet.getString("name"));
            genres.add(genre);
        }
        return genres;
    }

    @Override
    public Optional<Genre> get(long id) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM genres WHERE id = ?;", id);
        if (sqlRowSet.next()) {
            Genre genre = new Genre(sqlRowSet.getInt("id"), sqlRowSet.getString("name"));
            return Optional.of(genre);
        } else {
            return Optional.empty();
        }
    }

    public Set<Genre> getByFilmId(long filmId) {
        Set<Genre> genres = new HashSet<>();
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM films_genres WHERE film_id = ?;", filmId);
        while (sqlRowSet.next()) {
            long genreId = sqlRowSet.getInt("genre_id");
            SqlRowSet sqlRowSetGenres = jdbcTemplate.queryForRowSet("SELECT name FROM genres WHERE id = ?;", genreId);
            sqlRowSetGenres.next();
            Genre genre = new Genre(genreId, sqlRowSetGenres.getString("name"));
            genres.add(genre);
        }
        return genres;
    }
}