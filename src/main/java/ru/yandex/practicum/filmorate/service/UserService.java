package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Status;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage storage;

    @Autowired
    public UserService(@Qualifier("UserDbStorage") UserStorage storage) {
        this.storage = storage;
    }

    public User add(User user) {
        return storage.add(user);
    }

    public User update(User user) {
        return storage.update(user);
    }

    public List<User> getAll() {
        return storage.getAll();
    }

    public User get(long id) {
        return getUser(id);
    }

    public User addFriend(long id, long friendId) {
        ((UserDbStorage) storage).addFriend(id, friendId, Status.CONFIRMED);
        return getUser(id);
    }

    public User deleteFriend(long id, long friendId) {
        ((UserDbStorage) storage).deleteFriend(id, friendId);
        return getUser(id);
    }

    public List<User> getFriends(long id) {
        User user = getUser(id);
        return user
                .getFriends()
                .keySet()
                .stream()
                .map(this::getUser)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long id, long otherId) {
        User user = getUser(id);
        User otherUser = getUser(otherId);
        Set<Long> intersection = new HashSet<>(user.getFriends().keySet());
        intersection.retainAll(otherUser.getFriends().keySet());
        return intersection
                .stream()
                .map(this::getUser)
                .collect(Collectors.toList());
    }

    private User getUser(long id) {
        return storage.get(id).orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден.", id)));
    }
}