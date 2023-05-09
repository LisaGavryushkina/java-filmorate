package ru.yandex.practicum.filmorate.service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Service
@Slf4j
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
        User added = userStorage.add(user);
        log.info("Добавлен новый пользователь: {}", added);
        return added;
    }

    public User updateUser(User user) {
        User updated = userStorage.update(user);
        log.info("Информация о пользователе обновлена: {}", user);
        return updated;
    }

    public List<User> findAllUsers() {
        List<User> allUsers = userStorage.findAll();
        log.info("Вернули всех пользователей: {}", allUsers);
        return allUsers;
    }

    public User addFriend(int userId, int friendId) {
        User updated = userStorage.addFriend(userId, friendId);
        log.info("Пользователь [{}] добавил в друзья пользователя [{}]", userId, friendId);
        return updated;
    }

    public User deleteFriend(int userId, int friendId) {
        User updated = userStorage.deleteFriend(userId, friendId);
        log.info("Пользователь [{}] удалил из друзей пользователя [{}]", userId, friendId);
        return updated;
    }

    public List<User> getAllFriends(int userId) {
        List<User> friends = userStorage.getAllFriends(userId);
        log.info("Вернули список друзей для пользователя [{}] : [{}]", userId, friends);
        return friends;
    }

    public List<User> getAllCommonFriends(int userId, int otherId) {
        List<User> commonFriends = userStorage.getAllCommonFriends(userId, otherId);
        log.info("Вернули список общих друзей для пользователей [{}] и [{}]: {}", userId, otherId, commonFriends);
        return commonFriends;
    }

    public User findUser(int id) {
        User user = userStorage.getUser(id);
        log.info("Вернули пользователя [{}]", id);
        return user;
    }
}
