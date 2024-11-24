package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;


@EqualsAndHashCode(of = "id")
@Data
public class User {
    private Integer id;
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Адрес электронной почты должен быть в формате user@domain.ru")
    private String email;
    @NotBlank
    @Pattern(regexp = "^[^\\s]+$", message = "Логин не может быть пустым и содержать пробелы")
    private String login;
    private String name;
    @NotNull(message = "Дата рождения не может быть null")
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
