package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Status;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
public class FriendService {

    private final FriendStorage friendStorage;
    private final UserStorage userStorage;

    @Autowired
    public FriendService(FriendStorage friendStorage, UserStorage userStorage) {
        this.friendStorage = friendStorage;
        this.userStorage = userStorage;
    }

    public User addFriend(long id, long friendId) {
        if (userStorage.contains(id) && userStorage.contains(friendId)) {
            friendStorage.addFriend(id, friendId, Status.CONFIRMED);
            return userStorage.get(id);
        }
        return null;
    }

    public User deleteFriend(long id, long friendId) {
        if (userStorage.contains(id) && userStorage.contains(friendId)) {
            friendStorage.deleteFriend(id, friendId);
            return userStorage.get(id);
        }
        return null;
    }

    public List<User> getFriends(long id) {
        return friendStorage.getFriends(id);
    }

    public List<User> getCommonFriends(long id, long otherId) {
        return friendStorage.getCommonFriends(id, otherId);
    }
}