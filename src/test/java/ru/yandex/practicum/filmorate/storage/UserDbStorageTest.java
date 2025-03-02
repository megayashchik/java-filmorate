package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dto.UserFriendDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@Import(UserDbStorage.class)
@Sql(scripts = {"classpath:schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class UserDbStorageTest {

    private final UserDbStorage userDbStorage;
    private final JdbcTemplate jdbcTemplate;

    private User testUser1;
    private User testUser2;

    @Autowired
    public UserDbStorageTest(UserDbStorage userDbStorage, JdbcTemplate jdbcTemplate) {
        this.userDbStorage = userDbStorage;
        this.jdbcTemplate = jdbcTemplate;
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM friendship");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM friendship_statuses");
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE friendship_statuses ALTER COLUMN status_id RESTART WITH 1");

        jdbcTemplate.update("INSERT INTO friendship_statuses (name) " +
                "VALUES ('Подтверждённая'), ('Неподтверждённая')");

        testUser1 = createTestUser("user1@example.com", "login1");
        testUser2 = createTestUser("user2@example.com", "login2");

        Integer userCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        Integer statusCount = jdbcTemplate.queryForObject("SELECT COUNT(*)" +
                " FROM friendship_statuses", Integer.class);
    }

    private User createTestUser(String email, String login) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName("User");
        user.setBirthday(LocalDate.of(2000, 10, 11));
        return userDbStorage.createUser(user);
    }

    @Test
    void should_create_User() {
        Optional<User> savedUser = userDbStorage.findUserById(testUser1.getId());
        assertThat(savedUser)
                .isPresent()
                .get()
                .hasFieldOrPropertyWithValue("email", "user1@example.com");
    }

    @Test
    void should_update_User() {
        testUser1.setName("Новое Имя");
        userDbStorage.updateUser(testUser1);

        Optional<User> updatedUser = userDbStorage.findUserById(testUser1.getId());
        assertThat(updatedUser).get().hasFieldOrPropertyWithValue("name", "Новое Имя");
    }

    @Test
    void should_delete_User() {
        boolean isDeleted = userDbStorage.deleteUser(testUser1.getId());
        assertThat(isDeleted).isTrue();
        assertThat(userDbStorage.findUserById(testUser1.getId())).isEmpty();
    }

    @Test
    void should_find_All_Users() {
        Collection<User> users = userDbStorage.findAllUsers();
        assertThat(users)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(testUser1.getId(), testUser2.getId());
    }

    @Test
    void should_add_Friend() {
        userDbStorage.addFriend(1, 2);

        Integer statusId = jdbcTemplate.queryForObject(
                "SELECT status_id FROM friendship WHERE user_id = ? AND friend_id = ?",
                Integer.class,
                1, 2
        );

        assertThat(statusId).isEqualTo(1);
    }

    @Test
    void should_confirm_Friend() {
        userDbStorage.addFriend(1, 2);
        userDbStorage.confirmFriend(2, 1);

        Integer statusId = jdbcTemplate.queryForObject(
                "SELECT status_id FROM friendship WHERE user_id = ? AND friend_id = ?",
                Integer.class,
                1, 2
        );

        assertThat(statusId).isEqualTo(1);
    }

    @Test
    void should_delete_Friend() {
        userDbStorage.addFriend(1, 2);
        userDbStorage.deleteFriend(1, 2);

        Boolean exists = jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM friendship WHERE user_id = ? AND friend_id = ?)",
                Boolean.class,
                1, 2
        );

        assertThat(exists).isFalse();
    }

    @Test
    void should_find_User_Friends() {
        userDbStorage.addFriend(1, 2);

        Collection<User> friends = userDbStorage.findUserFriends(1);

        assertThat(friends)
                .extracting(User::getId)
                .containsExactly(2);
    }

    @Test
    void should_check_is_Friend() {
        userDbStorage.addFriend(1, 2);
        assertThat(userDbStorage.isFriend(1, 2)).isTrue();

        userDbStorage.confirmFriend(2, 1);
        assertThat(userDbStorage.isFriend(1, 2)).isTrue();
    }

    @Test
    void should_find_User_Friends_By_Status() {
        userDbStorage.addFriend(1, 2);

        Collection<UserFriendDto> confirmedFriendRequests = userDbStorage.findUserFriendsByStatus(1, 1);

        assertThat(confirmedFriendRequests)
                .extracting(UserFriendDto::getId)
                .containsExactly(2);
    }
}




