package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {

    private static final LocalDate CINEMATOGRAPHY_DATE = LocalDate.of(1895, 12, 28);

    private final Map<Long, Film> films = new LinkedHashMap<>();

    private int currentIdentifier = 0;

    @PostMapping(value = "/films")
    public Film add(@Valid @RequestBody Film film) {
        validateReleaseDate(film.getReleaseDate());
        film.setId(++currentIdentifier);
        films.put(film.getId(), film);
        log.info("Выполнен запрос POST /film");
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {
        validateReleaseDate(film.getReleaseDate());
        if (films.containsKey(film.getId())) {
            films.replace(film.getId(), film);
        } else {
            throw new FilmNotFoundException();
        }
        log.info("Выполнен запрос PUT /film.");
        return film;
    }

    @GetMapping("/films")
    public List<Film> getAll() {
        log.info("Выполнен запрос GET /films");
        return new ArrayList<>(films.values());
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        if (!releaseDate.isAfter(CINEMATOGRAPHY_DATE)) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года.");
        }
    }
}