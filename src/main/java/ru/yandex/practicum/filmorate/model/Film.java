package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.annotation.DateNotBefore;

import java.time.LocalDate;


@EqualsAndHashCode(of = "id")
@Data
@Slf4j
public class Film {
    private Integer id;
    @NotBlank(message = "Hазвание фильма не может быть пустым")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания фильма не должна превышать 200 символов")
    private String description;
    @DateNotBefore(message = "Дата релиза фильма не может быть раньше 28 декабря 1895 года")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration;
}