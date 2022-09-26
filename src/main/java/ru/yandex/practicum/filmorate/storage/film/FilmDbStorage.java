package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component("FilmDbStorage")
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
                film.getRate(),
                film.getMpa().getId()
        );
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM films ORDER BY id DESC LIMIT 1;");
        sqlRowSet.next();
        film.setId(sqlRowSet.getInt("id"));
        film.getGenres().forEach(genre ->
                jdbcTemplate.update("INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?);", film.getId(), genre.getId()));
    }

    @Override
    public void update(Film film) {
        SqlRowSet set = jdbcTemplate.queryForRowSet("SELECT * FROM films WHERE id = ?;", film.getId());
        if (set.next()) {
            jdbcTemplate.update(
                    "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rate = ?, mpa_id = ?;",
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate(),
                    film.getDuration(),
                    film.getRate(),
                    film.getMpa().getId()
            );
            jdbcTemplate.update("DELETE FROM films_genres WHERE film_id = ?;", film.getId());
            film.getGenres().forEach(genre ->
                    jdbcTemplate.update("INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?)", film.getId(), genre.getId()));
        } else {
            throw new FilmNotFoundException(String.format("Фильм с идентификатором %d не найден.", film.getId()));
        }
    }

    @Override
    public void delete(Film film) {

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

    public void addLike(long filmId, long userId) {
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?);", filmId, userId);
        jdbcTemplate.update("UPDATE films SET rate = rate + 1 WHERE id = ?;", filmId);
    }

    public void removeLike(long filmId, long userId) {
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?;", filmId, userId);
        jdbcTemplate.update("UPDATE films SET rate = rate - 1 WHERE id = ?;", filmId);
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
}