package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.*;

@Component("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film add(Film film) {
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
        return film;
    }

    @Override
    public Film update(Film film) {
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
            SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM films WHERE id = ?;", film.getId());
            sqlRowSet.next();
            return getFilm(sqlRowSet);
        } else {
            throw new FilmNotFoundException(String.format("Фильм с идентификатором %d не найден.", film.getId()));
        }
    }

    @Override
    public void delete(Film film) {

    }

    @Override
    public List<Film> getAll() {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM films;");
        List<Film> films = new ArrayList<>();
        while (sqlRowSet.next()) {
            Film film = getFilm(sqlRowSet);
            films.add(film);
        }
        return films;
    }

    @Override
    public Optional<Film> get(long id) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM films WHERE id = ?;", id);
        if (sqlRowSet.next()) {
            Film film = getFilm(sqlRowSet);
            return Optional.of(film);
        } else {
            return Optional.empty();
        }
    }

    public void addLike(long filmId, long userId) {
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?);", filmId, userId);
    }

    public void removeLike(long filmId, long userId) {
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?;", filmId, userId);
    }

    private Film getFilm(SqlRowSet sqlRowSet) {
        Mpa mpa = getMpa(sqlRowSet.getInt("mpa_id"));
        Set<Genre> genres = getGenres(sqlRowSet.getInt("id"));
        Set<Long> likes = getLikes(sqlRowSet.getInt("id"));
        Film film = Film
                .builder()
                .id(sqlRowSet.getInt("id"))
                .name(sqlRowSet.getString("name"))
                .description(sqlRowSet.getString("description"))
                .releaseDate(Objects.requireNonNull(sqlRowSet.getDate("release_date")).toLocalDate())
                .duration(sqlRowSet.getInt("duration"))
                .rate(sqlRowSet.getInt("rate"))
                .mpa(mpa)
                .build();
        genres.forEach(film::addGenre);
        likes.forEach(film::addLike);
        return film;
    }

    private Mpa getMpa(long id) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM mpa WHERE id = ?;", id);
        sqlRowSet.next();
        Mpa mpa = new Mpa();
        mpa.setId(sqlRowSet.getInt("id"));
        mpa.setName(sqlRowSet.getString("name"));
        return mpa;
    }

    private Set<Genre> getGenres(long id) {
        Set<Genre> genres = new HashSet<>();
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT * FROM films_genres WHERE film_id = ?;", id);
        while (sqlRowSet.next()) {
            long genreId = sqlRowSet.getInt("genre_id");
            SqlRowSet sqlRowSetGenres = jdbcTemplate.queryForRowSet("SELECT name FROM genres WHERE id = ?;", genreId);
            sqlRowSetGenres.next();
            Genre genre = new Genre(genreId, sqlRowSetGenres.getString("name"));
            genres.add(genre);
        }
        return genres;
    }

    private Set<Long> getLikes(long filmId) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT user_id FROM likes WHERE film_id = ?;", filmId);
        Set<Long> likes = new LinkedHashSet<>();
        while (sqlRowSet.next()) {
            long userId = sqlRowSet.getInt("user_id");
            likes.add(userId);
        }
        return likes;
    }
}