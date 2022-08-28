package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage storage;

    @Autowired
    public FilmService(InMemoryFilmStorage storage) {
        this.storage = storage;
    }

    public Film add(Film film) {
        return storage.add(film);
    }

    public Film update(Film film) {
        return storage.update(film);
    }

    public List<Film> getAll() {
        return storage.getAll();
    }

    public Film get(long id) {
        return storage.get(id).orElseThrow(() -> new FilmNotFoundException(String.format("Фильм с идентификатором %d не найден.", id)));
    }

    public Film addLike(long id, long userId) {
        Film film = storage.get(id).orElseThrow(() -> new FilmNotFoundException(String.format("Фильм с идентификатором %d не найден.", id)));
        film.addLike(userId);
        return film;
    }

    public Film removeLike(long id, long userId) {
        Film film = storage.get(id).orElseThrow(() -> new FilmNotFoundException(String.format("Фильм с идентификатором %d не найден.", id)));
        if (film.removeLike(userId)) {
            return film;
        } else {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден.", userId));
        }
    }

    public List<Film> getPopularFilms(int count) {
        return storage
                .getAll()
                .stream()
                .sorted((film1, film2) -> Integer.compare(film2.getRate(), film1.getRate()))
                .limit(count)
                .collect(Collectors.toList());
    }
}