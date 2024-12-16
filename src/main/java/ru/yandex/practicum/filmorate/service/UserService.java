package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DuplicateDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User createUser(User user) {
        log.debug("Добавление пользователя.");

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidateException("Адрес электронной почты не может быть пустым");
        }

        return userStorage.createUser(user);
    }

    public User updateUser(User newUser) {
        log.debug("Обновление пользователя.");

        if (newUser.getId() == null) {
            throw new NotFoundException("Пользователь с id= " + newUser.getId() + " не найден.");
        }

        return userStorage.updateUser(newUser);
    }

    public Collection<User> getAllUsers() {
        log.debug("Получение всех пользователей.");

        return userStorage.findAllUsers();
    }

    public void addFriend(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) {
            throw new ValidateException("Нельзя добавить самого себя в друзья.");
        }

        User user = userStorage.findUser(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = "
                + userId + " не найден."));
        User friend = userStorage.findUser(friendId).orElseThrow(() -> new NotFoundException("Пользователь с id = "
                + friendId + " не найден."));

        if (user.getFriends().contains(friendId)) {
            throw new DuplicateDataException(String.format("%s Пользователь уже является другом %s",
                    user.getName(), friend.getName()));
        }

        log.debug("Добавление {} в список друзей {}", friend.getName(), user.getName());

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        userStorage.updateUser(user);
        userStorage.updateUser(friend);
    }

    public void deleteFriend(Integer userId, Integer friendId) {
        User user = userStorage.findUser(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = "
                + userId + " не найден."));
        User friend = userStorage.findUser(friendId).orElseThrow(() -> new NotFoundException("Пользователь с id = "
                + friendId + " не найден."));

        log.debug("Удаление {} из списка друзей {}", friend.getName(), user.getName());

        if (user.getFriends().contains(friendId)) {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(userId);
            userStorage.updateUser(user);
            userStorage.updateUser(friend);
        } else {
            log.debug(String.format("Пользователь %s не является другом %s",
                    friend.getName(), user.getName()));
        }
    }

    public Collection<User> findUserFriends(Integer userId) {
        User user = userStorage.findUser(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = "
                + userId + " не найден."));
        Set<Integer> userFriends = user.getFriends();

        if (userFriends.isEmpty()) {
            return Collections.emptyList();
        }

        return userStorage.findAllUsers().stream()
                .filter(u -> userFriends.contains(u.getId()))
                .collect(Collectors.toList());
    }

    public Collection<User> findCommonFriends(Integer userId, Integer friendId) {
        log.debug("Получение списка общих друзей.");

        Collection<User> usersFriends = findUserFriends(userId);
        Collection<User> friendsOfFriends = findUserFriends(friendId);
        usersFriends.retainAll(friendsOfFriends);

        return usersFriends;
    }
}
