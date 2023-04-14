package ru.yandex.practicum.filmorate.storage.user;

import java.util.List;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {

    User add(User user);

    User update(User user);

    List<User> findAll();

    void clear();

    User getUser(int id);

    List<User> getAllFriends(int userId);

    List<User> getAllCommonFriends(int userId, int otherId);

    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);
}
