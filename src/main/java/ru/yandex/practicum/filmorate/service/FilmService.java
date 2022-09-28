package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final LikeStorage likeStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage, GenreStorage genreStorage, LikeStorage likeStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreStorage = genreStorage;
        this.likeStorage = likeStorage;
    }

    public Film add(Film film) {
        filmStorage.add(film);
        return getFilm(film.getId());
    }

    public Film update(Film film) {
        filmStorage.update(film);
        return getFilm(film.getId());
    }

    public List<Film> getAll() {
        List<Film> all = filmStorage.getAll();
        all.forEach(this::addGenres);
        return all;
    }

    public Film get(long id) {
        return getFilm(id);
    }

    public Film addLike(long id, long userId) {
        checkUserExists(userId);
        likeStorage.addLike(id, userId);
        return getFilm(id);
    }

    public Film removeLike(long id, long userId) {
        checkUserExists(userId);
        likeStorage.removeLike(id, userId);
        return getFilm(id);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage
                .getAll()
                .stream()
                .sorted((film1, film2) -> Integer.compare(film2.getRate(), film1.getRate()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private Film getFilm(long id) {
        return filmStorage
                .get(id)
                .map(film -> {
                    addGenres(film);
                    return film;
                })
                .orElseThrow(() -> new FilmNotFoundException(String.format("Фильм с идентификатором %d не найден.", id)));
    }

    private void checkUserExists(long id) {
        if (!userStorage.contains(id)) {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден.", id));
        }
    }

    private void addGenres(Film film) {
        Set<Genre> genres = genreStorage.getByFilmId(film.getId());
        genres.forEach(film::addGenre);
    }
}