package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;


class FilmorateApplicationTests {

    private User user;
    private UserController userController;

    @BeforeEach
    public void setUp() {
        user = new User();
        userController = new UserController();
    }

    @Test
    public void createUserWithEmptyName() {
        user.setId(1);
        user.setEmail("user@domain.ru");
        user.setLogin("qwerty");
        user.setName("");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        User createdUser = userController.create(user);

        assertEquals(user.getLogin(), createdUser.getName(), "Имя пользователя должно быть логином");
    }
}

