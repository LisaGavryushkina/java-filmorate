package ru.yandex.practicum.filmorate.controller;

import java.util.List;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void clear() {
        userService.clear();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        User added = userService.addUser(user);
        log.info("Добавлен новый пользователь: {}", user);
        return added;
    }

    @GetMapping("/{id}")
    public User findUser(@PathVariable int id) {
        return userService.findUser(id);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        User updated = userService.updateUser(user);
        log.info("Информация о пользователе обновлена: {}", user);
        return updated;
    }

    @GetMapping
    public List<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id, @PathVariable int friendId) {
        User updated = userService.addFriend(id, friendId);
        log.info("Пользователь с id: {} добавил в друзья пользователя с id: {}", id, friendId);
        return updated;
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        User updated = userService.deleteFriend(id, friendId);
        log.info("Пользователь с id: {} удалил из друзей пользователя с id: {}", id, friendId);
        return updated;
    }

    @GetMapping("/{id}/friends")
    public List<User> findAllFriends(@PathVariable int id) {
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findAllCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getAllCommonFriends(id, otherId);
    }
}
