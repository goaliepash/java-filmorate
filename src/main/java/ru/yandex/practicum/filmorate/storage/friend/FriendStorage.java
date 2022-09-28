package ru.yandex.practicum.filmorate.storage.friend;

import ru.yandex.practicum.filmorate.model.Status;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendStorage {

    void addFriend(long userId, long friendId, Status status);

    void deleteFriend(long userId, long friendId);

    List<User> getFriends(long id);

    List<User> getCommonFriends(long userId, long otherUserId);
}