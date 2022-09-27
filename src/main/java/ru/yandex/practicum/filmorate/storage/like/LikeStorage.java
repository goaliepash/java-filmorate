package ru.yandex.practicum.filmorate.storage.like;

public interface LikeStorage {

    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);
}