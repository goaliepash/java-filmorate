package ru.yandex.practicum.filmorate.storage.friend;

import ru.yandex.practicum.filmorate.model.Status;

import java.util.Set;

public interface FriendStorage {

    void addFriend(long userId, long friendId, Status status);

    void deleteFriend(long userId, long friendId);

    Set<Long> getFriends(long id);
}