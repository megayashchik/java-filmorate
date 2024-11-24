package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.annotation.DateNotBefore;

import java.time.LocalDate;


@EqualsAndHashCode(of = "id")
@Data
public class Film {
    private Integer id;
    @NotNull(message = "Hазвание фильма не может быть null")
    @NotBlank(message = "Hазвание фильма не может быть пустым")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания фильма не должна превышать 200 символов")
    private String description;
    @NotNull(message = "Дата релиза не может быть null")
    @DateNotBefore(message = "Дата релиза фильма не может быть раньше 28 декабря 1895 года")
    private LocalDate releaseDate;
    @NotNull(message = "Продолжительность фильма не может быть null")
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration;
}