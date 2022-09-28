package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void add(Film film) {
        jdbcTemplate.update(
                "INSERT INTO films (name, description, release_date, duration, rate, mpa_id) VALUES (?, ?, ?, ?, ?, ?);",
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                0,
                film.getMpa().getId()
        );
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM films ORDER BY id DESC LIMIT 1;");
        sqlRowSet.next();
        film.setId(sqlRowSet.getInt("id"));
        updateGenres(film);
    }

    @Override
    public void update(Film film) {
        SqlRowSet set = jdbcTemplate.queryForRowSet("SELECT * FROM films WHERE id = ?;", film.getId());
        if (set.next()) {
            jdbcTemplate.update(
                    "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?;",
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getMpa().getId()
            );
            jdbcTemplate.update("DELETE FROM films_genres WHERE film_id = ?;", film.getId());
            updateGenres(film);
        } else {
            throw new FilmNotFoundException(String.format("Фильм с идентификатором %d не найден.", film.getId()));
        }
    }

    @Override
    public void delete(Film film) {
        jdbcTemplate.update("DELETE FROM films_genres WHERE film_id = ?;", film.getId());
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ?;", film.getId());
        jdbcTemplate.update("DELETE FROM films WHERE id = ?;", film.getId());
    }

    @Override
    public List<Film> getAll() {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rate, f.mpa_id, m.mpa_name FROM films AS f " +
                "INNER JOIN mpa as m ON f.mpa_id = m.id;");
        List<Film> films = new ArrayList<>();
        while (sqlRowSet.next()) {
            Film film = getFilm(sqlRowSet);
            films.add(film);
        }
        return films;
    }

    @Override
    public Optional<Film> get(long id) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT f.id, f.name, f.description, f.release_date, f.duration, f.rate, f.mpa_id, m.mpa_name FROM films AS f " +
                "INNER JOIN mpa as m ON f.mpa_id = m.id " +
                "WHERE f.id = ?;", id);
        if (sqlRowSet.next()) {
            Film film = getFilm(sqlRowSet);
            return Optional.of(film);
        } else {
            return Optional.empty();
        }
    }

    private Film getFilm(SqlRowSet sqlRowSet) {
        return Film
                .builder()
                .id(sqlRowSet.getInt("id"))
                .name(sqlRowSet.getString("name"))
                .description(sqlRowSet.getString("description"))
                .releaseDate(Objects.requireNonNull(sqlRowSet.getDate("release_date")).toLocalDate())
                .duration(sqlRowSet.getInt("duration"))
                .rate(sqlRowSet.getInt("rate"))
                .mpa(new Mpa(sqlRowSet.getInt("mpa_id"), sqlRowSet.getString("mpa_name")))
                .build();
    }

    private void updateGenres(Film film) {
        String sql = "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?);";
        List<Genre> genres = new ArrayList<>(film.getGenres());
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Genre genre = genres.get(i);
                ps.setLong(1, film.getId());
                ps.setLong(2, genre.getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
    }
}