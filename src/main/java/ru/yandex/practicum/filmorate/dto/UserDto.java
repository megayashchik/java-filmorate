package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class UserDto {
    private Integer id;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Адрес электронной почты должен быть в формате user@domain.ru")
    private String email;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "^[^\\s]+$", message = "Логин не может содержать пробелы")
    private String login;

    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<Integer> friends = new HashSet<>();
}
