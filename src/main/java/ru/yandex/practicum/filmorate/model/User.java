package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
@EqualsAndHashCode(of = "id")
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
    Set<Integer> friends = new HashSet<>();
}
