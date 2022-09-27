package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
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
            return getUser(set);
        } else {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден.", user.getId()));
        }
    }

    @Override
    public void delete(User user) {
        jdbcTemplate.update("DELETE FROM friendship WHERE user_id = ? OR friend_id = ?;", user.getId(), user.getId());
        jdbcTemplate.update("DELETE FROM likes WHERE USER_ID = ?;", user.getId());
        jdbcTemplate.update("DELETE FROM users WHERE id = ?;", user.getId());
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
        return users;
    }

    @Override
    public User get(long id) {
        SqlRowSet set = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id = ?;", id);
        User user = null;
        if (set.next()) {
            user = getUser(set);
        }
        return Optional
                .ofNullable(user)
                .orElseThrow(() -> new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден.", id)));
    }

    @Override
    public boolean contains(long id) {
        SqlRowSet set = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id = ?;", id);
        if (!set.next()) {
            throw new UserNotFoundException(String.format("Пользователь с идентификатором %d не найден.", id));
        }
        return true;
    }

    private User getUser(SqlRowSet set) {
        return User
                .builder()
                .id(set.getInt("id"))
                .email(set.getString("email"))
                .login(set.getString("login"))
                .name(set.getString("name"))
                .birthday(Objects.requireNonNull(set.getDate("birthday")).toLocalDate())
                .build();
    }
}