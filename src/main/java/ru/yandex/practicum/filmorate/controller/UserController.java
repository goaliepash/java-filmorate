package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public User add(@Valid @RequestBody User user) {
        log.info("Выполнен запрос POST /users");
        return service.add(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Выполнен запрос PUT /users");
        return service.update(user);
    }

    @GetMapping
    public List<User> getAll() {
        log.info("Выполнен запрос GET /users");
        return service.getAll();
    }

    @GetMapping("/{id}")
    public User get(@PathVariable long id) {
        log.info("Выполнен запрос GET /users/{}", id);
        return service.get(id);
    }
}