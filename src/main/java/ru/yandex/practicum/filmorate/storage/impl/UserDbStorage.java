package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.UserFriendDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Repository
public class UserDbStorage extends BaseStorage<User> implements UserStorage {

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, UserDbStorage::mapRowToUser);
    }

    private static User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());

        return user;
    }

    @Override
    public User createUser(User user) {
        String sqlQuery = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        Integer id = insert(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        user.setId(id);

        return user;
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        update(sqlQuery, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());

        return user;
    }

    @Override
    public boolean deleteUser(Integer userId) {
        String sqlQuery = "DELETE FROM users WHERE user_id = ?";

        return delete(sqlQuery, userId);
    }

    @Override
    public Optional<User> findUserById(Integer userId) {
        String sqlQuery = "SELECT * FROM users WHERE user_id = ?";

        return findOne(sqlQuery, userId);
    }

    @Override
    public Collection<User> findAllUsers() {
        String sqlQuery = "SELECT * FROM users";

        return findMany(sqlQuery);
    }

    @Override
    public void addFriend(Integer userId, Integer friendId) {
        String sqlQuery = "INSERT INTO friendship (user_id, friend_id, status_id) VALUES (?, ?, 1)";
        jdbcTemplate.update(sqlQuery, userId, friendId);
    }

    @Override
    public void confirmFriend(Integer userId, Integer friendId) {
        String sql = "UPDATE friendship SET status_id = 1 WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, friendId, userId);
    }

    @Override
    public boolean deleteFriend(Integer userId, Integer friendId) {
        String sql = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";
        int rowsAffected = jdbcTemplate.update(sql, userId, friendId);

        return rowsAffected > 0;
    }

    @Override
    public Collection<User> findUserFriends(Integer userId) {
        String sqlQuery = "SELECT u.* FROM users u " +
                "JOIN friendship f ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ? AND f.status_id = 1";

        return findMany(sqlQuery, userId);
    }

    @Override
    public Collection<UserFriendDto> findUserFriendsByStatus(Integer userId, Integer statusId) {
        String sqlQuery = "SELECT u.user_id, u.name " +
                "FROM users u " +
                "JOIN friendship f ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ? AND f.status_id = ?";

        return jdbcTemplate.query(sqlQuery, (rs, rowNum) ->
                new UserFriendDto(rs.getInt("user_id"), rs.getString("name")), userId, statusId);
    }

    @Override
    public boolean isFriend(Integer userId, Integer friendId) {
        String sql = "SELECT COUNT(*) FROM friendship " +
                "WHERE user_id = ? AND friend_id = ? AND status_id = 1";

        return jdbcTemplate.queryForObject(sql, Integer.class, userId, friendId) > 0;
    }

    @Override
    public void updateUserFriendshipStatus(Integer userId, Integer friendId, Integer statusId) {
        String sql = "UPDATE friendship SET status_id = ? WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, statusId, userId, friendId);
    }
}

