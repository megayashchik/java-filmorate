package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;


@EqualsAndHashCode(of = "id")
@Data
@Slf4j
public class User {
    private Integer id;
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Адрес электронной почты должен быть в формате user@domain.ru")
    private String email;
    @NotBlank
    @Pattern(regexp = "^[^\\s]+$", message = "Логин не может быть пустым и содержать пробелы")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
