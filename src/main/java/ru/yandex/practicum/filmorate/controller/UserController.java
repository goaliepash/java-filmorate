package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class UserController {

    private final Map<Integer, User> users = new LinkedHashMap<>();

    private int currentIdentifier = 0;

    @PostMapping(value = "/users")
    public User add(@Valid @RequestBody User user) {
        user.setId(++currentIdentifier);
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Выполнен запрос POST /user");
        return user;
    }

    @PutMapping(value = "/users")
    public User update(@Valid @RequestBody User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.replace(user.getId(), user);
        log.info("Выполнен запрос PUT /user");
        return user;
    }

    @GetMapping("/users")
    public List<User> getAll() {
        log.info("Выполнен запрос GET /users");
        return new ArrayList<>(users.values());
    }
}