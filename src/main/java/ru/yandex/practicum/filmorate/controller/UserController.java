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

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.info("Запрос на добавление пользователя: {}", user);
        return userService.addUser(user);
    }

    @GetMapping("/{id}")
    public User findUser(@PathVariable int id) {
        log.info("Запрос на получение пользователя [{}]", id);
        return userService.findUser(id);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Запрос на обновление пользователя: {}", user);
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> findAllUsers() {
        log.info("Запрос на получение всех пользователей");
        return userService.findAllUsers();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Запрос на добавление пользователя [{}] в друзья к пользователю [{}]", friendId, id);
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Запрос на удаление пользователя [{}] из друзей пользователя [{}]", friendId, id);
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> findAllFriends(@PathVariable int id) {
        log.info("Запрос на получение списка всех друзей пользователя [{}]", id);
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findAllCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Запрос на получение списка общих друзей пользователя [{}] с пользователем [{}]", id, otherId);
        return userService.getAllCommonFriends(id, otherId);
    }
}
