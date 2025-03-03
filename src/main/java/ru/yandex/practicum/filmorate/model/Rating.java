package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Rating {
    private Integer id;

    @NotNull(message = "Название рейтинга не должно быть null")
    @NotBlank(message = "Название рейтинга не должно быть пустым")
    private String name;
}
