package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;


@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        log.info("Создание пользователя {}.", user.getName());
        return userService.createUser(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        log.info("Обновление данных пользователя {}.", newUser.getName());
        return userService.updateUser(newUser);
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Список всех пользователей.");
        return userService.getAllUsers();
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Integer userId, @PathVariable("friendId") Integer friendId) {
        log.info("Пользователь с id = {} добавил в друзья пользователя с id = {}", userId, friendId);
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") Integer userId, @PathVariable("friendId") Integer friendId) {
        log.info("Пользователь с id = {} удалил из друзей пользователя с id = {}.", userId, friendId);
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> findUserFriends(@PathVariable("id") Integer userId) {
        log.info("Друзья пользователя {}", userId);
        return userService.findUserFriends(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> findCommonFriends(@PathVariable("id") Integer userId,
                                              @PathVariable("otherId") Integer otherId) {
        log.info("Общие друзья пользователей с id = {} и id = {}", userId, otherId);
        return userService.findCommonFriends(userId, otherId);
    }
}
