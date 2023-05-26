package ru.yandex.practicum.filmorate.storage.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;


public class InMemoryUserStorage implements UserStorage {

    private final Map<Integer, User> users = new HashMap<>();
    private int id;

    private int createId() {
        return ++id;
    }

    @Override
    public User add(User user) {
        user.setId(createId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            return user;
        }
        throw new UserNotFoundException(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void clear() {
        users.clear();
        id = 0;
    }

    @Override
    public User getUser(int id) {
        return Optional.ofNullable(users.get(id)).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public User addFriend(int userId, int friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        user.addFriend(friendId);
        return user;
    }

    @Override
    public User deleteFriend(int userId, int friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        user.deleteFriend(friendId);
        return user;
    }

    @Override
    public List<User> getAllFriends(int userId) {
        List<Integer> userFriends = getUser(userId).getAllFriends();

        return userFriends.stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getAllCommonFriends(int userId, int otherId) {
        List<Integer> userFriends = getUser(userId).getAllFriends();
        List<Integer> otherUserFriends = getUser(otherId).getAllFriends();

        return userFriends.stream()
                .filter(otherUserFriends::contains)
                .map(users::get)
                .collect(Collectors.toList());
    }

}
