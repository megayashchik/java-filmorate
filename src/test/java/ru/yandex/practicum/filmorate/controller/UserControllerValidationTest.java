package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.UserDto;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerValidationTest {

    private Validator validator;
    private UserDto userDto;

    @BeforeEach
    public void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        userDto = new UserDto();
    }

    @Test
    public void create_Valid_User() {
        userDto.setEmail("user@domain.ru");
        userDto.setLogin("qwerty");
        userDto.setName("Имя");
        userDto.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertEquals(0, violations.size());
        assertTrue(violations.isEmpty(), "Ошибка при заполнении полей класса");
    }

    @Test
    public void create_User_With_Empty_Email() {
        userDto.setEmail("");
        userDto.setLogin("qwerty");
        userDto.setName("Имя");
        userDto.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertEquals(1, violations.size());
        assertFalse(violations.isEmpty(), "Email не может быть пустым");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    public void create_User_With_Null_Email() {
        userDto.setEmail(null);
        userDto.setLogin("qwerty");
        userDto.setName("Имя");
        userDto.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertEquals(1, violations.size());
        assertFalse(violations.isEmpty(), "Email не может быть пустым");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    public void create_User_With_Not_Valid_Email() {
        userDto.setEmail("user.email.ru");
        userDto.setLogin("qwerty");
        userDto.setName("Имя");
        userDto.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertEquals(1, violations.size());
        assertFalse(violations.isEmpty(), "Адрес электронной почты должен быть в формате user@domain.ru");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    public void create_User_With_Empty_Login() {
        userDto.setEmail("user@domain.ru");
        userDto.setLogin("");
        userDto.setName("Имя");
        userDto.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertEquals(2, violations.size());
        assertFalse(violations.isEmpty(), "Логин не может быть пустым");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("login")));
    }

    @Test
    public void create_User_With_Null_Login() {
        userDto.setEmail("user@domain.ru");
        userDto.setLogin(null);
        userDto.setName("Имя");
        userDto.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertEquals(1, violations.size());
        assertFalse(violations.isEmpty(), "Логин не может быть пустым");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("login")));
    }

    @Test
    public void create_User_With_Login_Containing_Spaces() {
        userDto.setEmail("user@domain.ru");
        userDto.setLogin("login with space");
        userDto.setName("Имя");
        userDto.setBirthday(LocalDate.of(2000, 1, 1));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertEquals(1, violations.size());
        assertFalse(violations.isEmpty(), "Логин не может содержать пробелы");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("login")));
    }

    @Test
    public void create_User_With_Future_Birthday() {
        userDto.setEmail("user@domain.ru");
        userDto.setLogin("qwerty");
        userDto.setName("Имя");
        userDto.setBirthday(LocalDate.of(2999, 1, 1));

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);

        assertEquals(1, violations.size());
        assertFalse(violations.isEmpty(), "Дата рождения не может быть в будущем");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("birthday")));
    }
}
