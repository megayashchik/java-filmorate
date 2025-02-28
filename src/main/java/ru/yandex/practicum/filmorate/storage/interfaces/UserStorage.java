package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.dto.UserFriendDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    User createUser(User user);

    User updateUser(User user);

    boolean deleteUser(Integer userId);

    Optional<User> findUserById(Integer userId);

    Collection<User> findAllUsers();

    void addFriend(Integer userId, Integer friendId);

    void confirmFriend(Integer userId, Integer friendId);

    void deleteFriend(Integer userId, Integer friendId);

    Collection<User> findUserFriends(Integer userId);

    Collection<UserFriendDto> findUserFriendsByStatus(Integer userId, Integer statusId);

    boolean isFriend(Integer userId, Integer friendId);

    void updateUserFriendshipStatus(Integer userId, Integer friendId, Integer statusId);
}
