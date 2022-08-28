package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new LinkedHashMap<>();

    private int currentIdentifier = 0;

    @Override
    public User add(User user) {
        user.setId(++currentIdentifier);
        setUserName(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        setUserName(user);
        if (users.containsKey(user.getId())) {
            users.replace(user.getId(), user);
        } else {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден.", user.getId()));
        }
        users.replace(user.getId(), user);
        return user;
    }

    @Override
    public boolean delete(User user) {
        if (users.containsKey(user.getId())) {
            users.remove(user.getId());
            return true;
        }
        return false;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> get(long id) {
        if (users.containsKey(id)) {
            return Optional.of(users.get(id));
        } else {
            return Optional.empty();
            // throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден.", id));
        }
    }

    private void setUserName(User user) {
        if (Objects.isNull(user.getName()) || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}