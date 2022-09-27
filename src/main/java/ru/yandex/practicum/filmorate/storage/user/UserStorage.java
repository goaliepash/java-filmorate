package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    User add(User user);

    User update(User user);

    boolean delete(User user);

    List<User> getAll();

    User get(long id);

    boolean contains(long id);
}