package ru.yandex.practicum.filmorate.storage.like;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addLike(long filmId, long userId) {
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?);", filmId, userId);
        updateRate(filmId);
    }

    public void removeLike(long filmId, long userId) {
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ? AND user_id = ?;", filmId, userId);
        updateRate(filmId);
    }

    private void updateRate(long filmId) {
        jdbcTemplate.update(
                "UPDATE films AS f SET rate = (SELECT COUNT(l.user_id) FROM likes AS l WHERE l.film_id = f.id) WHERE id = ?;",
                filmId
        );
    }
}