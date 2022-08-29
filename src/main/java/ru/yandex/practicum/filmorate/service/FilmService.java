package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film add(Film film) {
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film get(long id) {
        return getFilm(id);
    }

    public Film addLike(long id, long userId) {
        checkUserExists(userId);
        Film film = getFilm(id);
        film.addLike(userId);
        return film;
    }

    public Film removeLike(long id, long userId) {
        checkUserExists(userId);
        Film film = getFilm(id);
        film.removeLike(userId);
        return film;
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
                .orElseThrow(() -> new FilmNotFoundException(String.format("Фильм с идентификатором %d не найден.", id)));
    }

    private void checkUserExists(long id) {
        if (!userStorage.contains(id)) {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден.", id));
        }
    }
}