package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FriendService;

import java.util.List;

@RestController
@RequestMapping("/users/{id}/friends")
@Slf4j
public class FriendController {

    private final FriendService service;

    @Autowired
    public FriendController(FriendService service) {
        this.service = service;
    }

    @PutMapping("/{friendId}")
    public User addFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Выполнен запрос PUT /users/{}/friends/{}", id, friendId);
        return service.addFriend(id, friendId);
    }

    @DeleteMapping("/{friendId}")
    public User deleteFriend(@PathVariable long id, @PathVariable long friendId) {
        log.info("Выполнен запрос DELETE /users/{}/friends/{}", id, friendId);
        return service.deleteFriend(id, friendId);
    }

    @GetMapping("")
    public List<User> getFriends(@PathVariable long id) {
        log.info("Выполнен запрос GET /{}/friends", id);
        return service.getFriends(id);
    }

    @GetMapping("/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable long id, @PathVariable long otherId) {
        log.info("Выполнен запрос GET /users/{}/friends/common/{}", id, otherId);
        return service.getCommonFriends(id, otherId);
    }
}
