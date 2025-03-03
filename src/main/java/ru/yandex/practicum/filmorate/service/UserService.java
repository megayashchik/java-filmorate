package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        User createdUser = userStorage.createUser(user);

        return UserMapper.toDto(createdUser);
    }

    public UserDto updateUser(UserDto userDto) {
        log.info("Обновление user с DTO: {}", userDto);
        User existingUser = userStorage.findUserById(userDto.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userDto.getId() + " не найден"));
        User user = UserMapper.toEntity(userDto);
        User updatedUser = userStorage.updateUser(user);

        return UserMapper.toDto(updatedUser);
    }

    public void deleteUser(Integer userId) {
        userStorage.deleteUser(userId);
    }

    public UserDto getUserById(Integer userId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        return UserMapper.toDto(user);
    }

    public List<UserDto> getAllUsers() {
        return userStorage.findAllUsers().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public void addFriend(Integer userId, Integer friendId) {
        userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        userStorage.findUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + friendId + " не найден"));
        userStorage.addFriend(userId, friendId);
    }

    public void confirmFriend(Integer userId, Integer friendId) {
        userStorage.confirmFriend(userId, friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        log.info("Попытка удалить друга: userId={}, friendId={}", userId, friendId);
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        User friend = userStorage.findUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + friendId + " не найден"));

        if (!userStorage.isFriend(userId, friendId)) {
            log.info("Дружба не найдена между userId={} и friendId={}, нечего удалять", userId, friendId);
            return;
        }

        userStorage.deleteFriend(userId, friendId);
        log.info("Дружба удалена между userId={} и friendId={}", userId, friendId);
    }

    public List<UserDto> getUserFriends(Integer userId) {
        userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));

        return userStorage.findUserFriends(userId).stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<UserDto> getCommonFriends(Integer userId, Integer otherId) {
        userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + userId + " не найден"));
        userStorage.findUserById(otherId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id=" + otherId + " не найден"));
        Collection<User> friendsUser = userStorage.findUserFriends(userId);
        Collection<User> friendsOther = userStorage.findUserFriends(otherId);

        return friendsUser.stream()
                .filter(friendsOther::contains)
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }
}