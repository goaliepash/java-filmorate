package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Status;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        friendStorage.addFriend(id, friendId, Status.CONFIRMED);
        return userStorage.get(id);
    }

    public User deleteFriend(long id, long friendId) {
        friendStorage.deleteFriend(id, friendId);
        return userStorage.get(id);
    }

    public List<User> getFriends(long id) {
        Set<Long> friendsId = friendStorage.getFriends(id);
        return friendsId.stream().map(userStorage::get).collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long id, long otherId) {
        Set<Long> intersection = new HashSet<>(friendStorage.getFriends(id));
        intersection.retainAll(friendStorage.getFriends(otherId));
        return intersection
                .stream()
                .map(userStorage::get)
                .collect(Collectors.toList());
    }
}