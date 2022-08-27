package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService service;

    @Autowired
    public FilmController(FilmService service) {
        this.service = service;
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        log.info("Выполнен запрос POST /film");
        return service.add(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Выполнен запрос PUT /film.");
        return service.update(film);
    }

    @GetMapping
    public List<Film> getAll() {
        log.info("Выполнен запрос GET /films");
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Film get(@PathVariable long id) {
        log.info("Выполнен запрос GET /films/{}", id);
        return service.get(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Выполнен запрос PUT /films/{id}/like/{userId}");
        return service.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable long id, @PathVariable long userId) {
        log.info("Выполнен запрос DELETE /films/{}/like/{}", id, userId);
        return service.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false, name = "count", defaultValue = "10") String count) {
        log.info("Выполнен запрос GET /films/popular?count={}", count);
        return service.getPopularFilms(Integer.parseInt(count));
    }
}