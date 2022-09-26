package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {

    void add(Film film);

    void update(Film film);

    void delete(Film film);

    List<Film> getAll();

    Optional<Film> get(long id);
}