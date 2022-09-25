package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Status;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User add(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        jdbcTemplate.update(
                "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?);",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        SqlRowSet set = jdbcTemplate.queryForRowSet("SELECT * FROM users ORDER BY id DESC LIMIT 1;");
        set.next();
        user.setId(set.getInt("id"));
        return user;
    }

    @Override
    public User update(User user) {
        SqlRowSet set = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id = ?;", user.getId());
        if (set.next()) {
            jdbcTemplate.update(
                    "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?;",
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday(),
                    user.getId()
            );
            set = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id = ?;", user.getId());
            set.next();
            return User
                    .builder()
                    .id(set.getInt("id"))
                    .email(set.getString("email"))
                    .login(set.getString("login"))
                    .name(set.getString("name"))
                    .birthday(Objects.requireNonNull(set.getDate("birthday")).toLocalDate())
                    .build();
        } else {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден.", user.getId()));
        }
    }

    @Override
    public boolean delete(User user) {
        return false;
    }

    @Override
    public List<User> getAll() {
        SqlRowSet set = jdbcTemplate.queryForRowSet("SELECT * FROM users;");
        List<User> users = new ArrayList<>();
        while (set.next()) {
            User user = User
                    .builder()
                    .id(set.getInt("id"))
                    .email(set.getString("email"))
                    .login(set.getString("login"))
                    .name(set.getString("name"))
                    .birthday(Objects.requireNonNull(set.getDate("birthday")).toLocalDate())
                    .build();
            users.add(user);
        }
        set = jdbcTemplate.queryForRowSet("SELECT * FROM friendship;");
        while (set.next()) {
            int userId = set.getInt("user_id");
            int friendId = set.getInt("friend_id");
            Status status = Status.valueOf(set.getString("status"));
            users
                    .stream()
                    .filter(user -> user.getId() == userId)
                    .forEach(user -> user.addFriend(friendId, status));
        }
        return users;
    }

    @Override
    public Optional<User> get(long id) {
        SqlRowSet set = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id = ?;", id);
        if (set.next()) {
            User user = User
                    .builder()
                    .id(set.getInt("id"))
                    .email(set.getString("email"))
                    .login(set.getString("login"))
                    .name(set.getString("name"))
                    .birthday(Objects.requireNonNull(set.getDate("birthday")).toLocalDate())
                    .build();
            SqlRowSet friendsSet = jdbcTemplate.queryForRowSet("SELECT * FROM friendship WHERE user_id = ?;", id);
            while (friendsSet.next()) {
                user.addFriend(
                        friendsSet.getInt("friend_id"),
                        Status.valueOf(friendsSet.getString("status"))
                );
            }
            return Optional.of(user);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public boolean contains(long id) {
        SqlRowSet set = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id = ?;", id);
        if (!set.next()) {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден.", id));
        }
        return true;
    }

    public void addFriend(long userId, long friendId, Status status) {
        if (contains(userId) && contains(friendId)) {
            jdbcTemplate.update(
                    "INSERT INTO friendship (user_id, friend_id, status) VALUES (?, ?, ?);",
                    userId,
                    friendId,
                    status.name()
            );
        }
    }

    public void deleteFriend(long userId, long friendId) {
        if (contains(userId) && contains(friendId)) {
            jdbcTemplate.update("DELETE FROM friendship WHERE user_id = ? AND friend_id = ?;", userId, friendId);
        }
    }
}