package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.annotation.DateNotBefore;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class FilmDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;

    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;

    @NotNull(message = "Описание фильма не может быть null")
    @Size(max = 200, message = "Максимальная длина описания фильма не должна превышать 200 символов")
    private String description;

    @NotNull(message = "Дата релиза не может быть null")
    @DateNotBefore(message = "Дата релиза фильма не может быть раньше 28 декабря 1895 года")
    private LocalDate releaseDate;

    @NotNull(message = "Продолжительность фильма не может быть null")
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration;

    @NotNull(message = "Рейтинг MPA не может быть null")
    private RatingDto mpa;

    private List<GenreDto> genres;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<Integer> likes = new HashSet<>();
}