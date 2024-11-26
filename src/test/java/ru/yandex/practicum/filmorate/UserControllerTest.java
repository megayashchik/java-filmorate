package ru.yandex.practicum.filmorate;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {
    private User user;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        user = new User();
    }

    @Test
    public void createValidUser() {
        user.setId(1);
        user.setEmail("user@domain.ru");
        user.setLogin("qwerty");
        user.setName("Имя");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(0, violations.size());
        assertTrue(violations.isEmpty(), "Ошибка при заполнение полей класса");
    }

    @Test
    public void createUserWithEmptyEmail() {
        user.setId(1);
        user.setEmail("");
        user.setLogin("qwerty");
        user.setName("Имя");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
        assertFalse(violations.isEmpty(), "Email не может быть пустым");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().contains("email")));
    }

    @Test
    public void createUserWithNotValidEmail() {
        user.setId(1);
        user.setEmail("user.email.ru");
        user.setLogin("qwerty");
        user.setName("Имя");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
        assertFalse(violations.isEmpty(), "Адрес электронной почты должен быть в формате user@domain.ru");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().contains("email")));
    }

    @Test
    public void createUserWithEmptyLogin() {
        user.setId(1);
        user.setEmail("user@domain.ru");
        user.setLogin("");
        user.setName("Имя");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(2, violations.size());
        assertFalse(violations.isEmpty(), "Логин не может быть пустым");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().contains("login")));
    }

    @Test
    public void createUserWithLoginContainingSpaces() {
        user.setId(1);
        user.setEmail("user@domain.ru");
        user.setLogin(" ");
        user.setName("Имя");
        user.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(2, violations.size());
        assertFalse(violations.isEmpty(), "Логин не может содержать пробелы");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().contains("login")));
    }

    @Test
    public void createUserWithIncorrectBirthdayDate() {
        user.setId(1);
        user.setEmail("user@domain.ru");
        user.setLogin("qwerty");
        user.setName("Имя");
        user.setBirthday(LocalDate.of(2999, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size());
        assertFalse(violations.isEmpty(), "Дата рождения не может быть в будущем");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().contains("birthday")));
    }
}
