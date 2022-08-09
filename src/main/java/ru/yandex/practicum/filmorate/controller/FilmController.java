package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
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

    private final Map<Integer, Film> films = new LinkedHashMap<>();

    private int currentIdentifier = 0;

    @PostMapping(value = "/films")
    public Film add(@Valid @RequestBody Film film) {
        if (!validateReleaseDate(film.getReleaseDate())) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года.");
        }
        film.setId(++currentIdentifier);
        films.put(film.getId(), film);
        log.info("Выполнен запрос POST /film");
        return film;
    }

    @PutMapping(value = "/films")
    public Film update(@Valid @RequestBody Film film) {
        if (!validateReleaseDate(film.getReleaseDate())) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года.");
        }
        films.replace(film.getId(), film);
        log.info("Выполнен запрос PUT /film.");
        return film;
    }

    @GetMapping("/films")
    public List<Film> getAll() {
        log.info("Выполнен запрос GET /films");
        return new ArrayList<>(films.values());
    }

    private boolean validateReleaseDate(LocalDate releaseDate) {
        return releaseDate.isAfter(LocalDate.of(1895, 12, 28));
    }
}