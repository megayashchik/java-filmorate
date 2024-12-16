package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;


public interface UserStorage {

    User createUser(User user);

    User updateUser(User newUser);

    void deleteUser(Integer userId);

    Optional<User> findUser(Integer userId);

    Collection<User> findAllUsers();


}
