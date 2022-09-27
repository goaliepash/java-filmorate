package ru.yandex.practicum.filmorate.storage.friend;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Status;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.LinkedHashSet;
import java.util.Set;

@Repository
public class FriendDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    public FriendDbStorage(JdbcTemplate jdbcTemplate, UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.userStorage = userStorage;
    }

    public void addFriend(long userId, long friendId, Status status) {
        if (userStorage.contains(userId) && userStorage.contains(friendId)) {
            jdbcTemplate.update(
                    "INSERT INTO friendship (user_id, friend_id, status) VALUES (?, ?, ?);",
                    userId,
                    friendId,
                    status.name()
            );
        }
    }

    public void deleteFriend(long userId, long friendId) {
        if (userStorage.contains(userId) && userStorage.contains(friendId)) {
            jdbcTemplate.update("DELETE FROM friendship WHERE user_id = ? AND friend_id = ?;", userId, friendId);
        }
    }

    public Set<Long> getFriends(long id) {
        Set<Long> friends = new LinkedHashSet<>();
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("SELECT friend_id FROM friendship WHERE user_id = ?;", id);
        while (sqlRowSet.next()) {
            friends.add(sqlRowSet.getLong("friend_id"));
        }
        return friends;
    }
}