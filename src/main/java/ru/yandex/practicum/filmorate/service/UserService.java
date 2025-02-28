package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        user = userStorage.createUser(user);
        return UserMapper.toDto(user);
    }

    public UserDto updateUser(UserDto userDto) {
        User user = UserMapper.toEntity(userDto);
        user = userStorage.updateUser(user);
        return UserMapper.toDto(user);
    }

    public void deleteUser(Integer userId) {
        userStorage.deleteUser(userId);
    }

    public UserDto getUserById(Integer userId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return UserMapper.toDto(user);
    }

    public List<UserDto> getAllUsers() {
        return userStorage.findAllUsers().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public void addFriend(Integer userId, Integer friendId) {
        userStorage.addFriend(userId, friendId);
    }

    public void confirmFriend(Integer userId, Integer friendId) {
        userStorage.confirmFriend(userId, friendId);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        userStorage.deleteFriend(userId, friendId);
    }

    public List<UserDto> getUserFriends(Integer userId) {
        return userStorage.findUserFriends(userId).stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }
}