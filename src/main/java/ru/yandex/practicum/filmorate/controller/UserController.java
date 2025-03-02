package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        UserDto created = userService.createUser(userDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос на обновление для user: {}", userDto);
        UserDto updated = userService.updateUser(userDto);

        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Integer id) {
        UserDto userDto = userService.getUserById(id);

        return ResponseEntity.ok(userDto);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();

        return ResponseEntity.ok(users);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.addFriend(id, friendId);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/friends/{friendId}/confirm")
    public ResponseEntity<Void> confirmFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.confirmFriend(id, friendId);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        log.info("Получен запрос на удаление для userId={} и friendId={}", id, friendId);
        userService.deleteFriend(id, friendId);
        log.info("Друг успешно удалён для userId={} и friendId={}", id, friendId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/friends")
    public ResponseEntity<List<UserDto>> getUserFriends(@PathVariable Integer userId) {
        List<UserDto> friends = userService.getUserFriends(userId);

        return ResponseEntity.ok(friends);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public ResponseEntity<List<UserDto>> getCommonFriends(@PathVariable Integer userId, @PathVariable Integer otherId) {
        List<UserDto> common = userService.getCommonFriends(userId, otherId);

        return ResponseEntity.ok(common);
    }
}
