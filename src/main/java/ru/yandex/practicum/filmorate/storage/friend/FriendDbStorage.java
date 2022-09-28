package ru.yandex.practicum.filmorate.storage.friend;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Status;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Repository
public class FriendDbStorage implements FriendStorage {

    private final JdbcTemplate jdbcTemplate;

    public FriendDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addFriend(long userId, long friendId, Status status) {
        jdbcTemplate.update(
                "INSERT INTO friendship (user_id, friend_id, status) VALUES (?, ?, ?);",
                userId,
                friendId,
                status.name()
        );

    }

    public void deleteFriend(long userId, long friendId) {
        jdbcTemplate.update("DELETE FROM friendship WHERE user_id = ? AND friend_id = ?;", userId, friendId);

    }

    @Override
    public List<User> getFriends(long id) {
        List<User> friends = new ArrayList<>();
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(
                "SELECT * FROM users AS u INNER JOIN friendship AS f ON u.ID = f.FRIEND_ID WHERE f.user_id = ?;",
                id
        );
        while (sqlRowSet.next()) {
            friends.add(getUser(sqlRowSet));
        }
        return friends;
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherUserId) {
        Set<User> intersection = new HashSet<>(getFriends(userId));
        intersection.retainAll(getFriends(otherUserId));
        return new ArrayList<>(intersection);
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