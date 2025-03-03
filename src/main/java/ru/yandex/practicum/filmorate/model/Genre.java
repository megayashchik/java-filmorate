package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = "id")

public class Genre {
    private Integer id;

    @NotNull(message = "Название жанра не должно быть null")
    @NotBlank(message = "Название жанра не должно быть пустым")
    private String name;
}
