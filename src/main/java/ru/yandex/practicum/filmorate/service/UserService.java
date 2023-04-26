package ru.yandex.practicum.filmorate.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void clear() {
        userStorage.clear();
    }

    public User addUser(User user) {
        return userStorage.add(user);
    }

    public User updateUser(User user) {
        return userStorage.update(user);
    }

    public List<User> findAllUsers() {
        return userStorage.findAll();
    }

    public User addFriend(int userId, int friendId) {
        return userStorage.addFriend(userId, friendId);
    }

    public User deleteFriend(int userId, int friendId) {
        return userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getAllFriends(int userId) {
        return userStorage.getAllFriends(userId);
    }

    public List<User> getAllCommonFriends(int userId, int otherId) {
        return userStorage.getAllCommonFriends(userId, otherId);
    }

    public User findUser(int id) {
        return userStorage.getUser(id);
    }
}
