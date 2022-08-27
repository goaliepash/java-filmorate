package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private static final LocalDate CINEMATOGRAPHY_DATE = LocalDate.of(1895, 12, 28);

    private final Map<Long, Film> films = new LinkedHashMap<>();

    private int currentIdentifier = 0;

    @Override
    public Film add(Film film) {
        validateReleaseDate(film.getReleaseDate());
        film.setId(++currentIdentifier);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        validateReleaseDate(film.getReleaseDate());
        if (films.containsKey(film.getId())) {
            films.replace(film.getId(), film);
        } else {
            throw new FilmNotFoundException(String.format("Фильм с идентификатором %d не найден.", film.getId()));
        }
        return film;
    }

    @Override
    public void delete(Film film) {
        validateReleaseDate(film.getReleaseDate());
        films.remove(film.getId());
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film get(long id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new FilmNotFoundException(String.format("Фильм с идентификатором %d не найден.", id));
        }
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        if (!releaseDate.isAfter(CINEMATOGRAPHY_DATE)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года.");
        }
    }
}