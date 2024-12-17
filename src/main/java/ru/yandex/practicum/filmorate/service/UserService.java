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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User findUser(Integer userId) {
        return userStorage.findUser(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = "
                + userId + " не найден."));
    }

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
            throw new ValidateException("Пользователь с id= " + newUser.getId() + " не найден.");
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

        User user = findUser(userId);
        User friend = findUser(friendId);

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
        User user = findUser(userId);
        User friend = findUser(friendId);

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
        User user = findUser(userId);
        Set<Integer> userFriends = user.getFriends();

        log.debug("Список друзей {}", user.getName());

        if (userFriends.isEmpty()) {
            return Collections.emptyList();
        }

        return userFriends.stream()
                .map(userStorage::findUser)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    public Collection<User> findCommonFriends(Integer userId, Integer friendId) {
        log.debug("Получение списка общих друзей.");

        Set<Integer> usersFriends = findUser(userId).getFriends();
        Set<Integer> friendsOfFriends = findUser(friendId).getFriends();
        usersFriends.retainAll(friendsOfFriends);

        return usersFriends.stream()
                .map(id -> findUser(id))
                .collect(Collectors.toList());
    }
}
