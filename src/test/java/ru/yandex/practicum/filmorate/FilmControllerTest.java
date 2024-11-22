package ru.yandex.practicum.filmorate;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private Film film;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
        film = new Film();
    }

    @Test
    public void createValidFilm() {
        film.setId(1);
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2024, 11, 20));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(0, violations.size());
        assertTrue(violations.isEmpty(), "Ошибка при заполнение полей класса");
    }

    @Test
    public void createFilmWithEmptyName() {
        film.setId(1);
        film.setName("");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2024, 11, 20));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
        assertFalse(violations.isEmpty(), "Hазвание фильма не может быть пустым");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().contains("name")));
    }

    @Test
    public void createFilmWithEmptyDescription() {
        film.setId(1);
        film.setName("Название фильма");
        film.setDescription("Описание фильма".repeat(20));
        film.setReleaseDate(LocalDate.of(2024, 11, 20));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
        assertFalse(violations.isEmpty(),
                "Максимальная длина описания фильма не должна превышать 200 символов");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().contains("description")));
    }

    @Test
    public void createFilmBeforeDateRelease() {
        film.setId(1);
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(1894, 12, 28));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
        assertFalse(violations.isEmpty(), "Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().contains("releaseDate")));
    }

    @Test
    public void createFilmWithSameDateRelease() {
        film.setId(1);
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(0, violations.size());
        assertTrue(violations.isEmpty(), "Дата релиза фильма может быть 28 декабря 1895 года");
    }

    @Test
    public void createFilmWithNegativeDuration() {
        film.setId(1);
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2024, 11, 20));
        film.setDuration(-1);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
        assertFalse(violations.isEmpty(), "Продолжительность фильма должна быть положительным числом");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().contains("duration")));
    }

    @Test
    public void createFilmWithZeroDuration() {
        film.setId(1);
        film.setName("Название фильма");
        film.setDescription("Описание фильма");
        film.setReleaseDate(LocalDate.of(2024, 11, 20));
        film.setDuration(0);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertEquals(1, violations.size());
        assertFalse(violations.isEmpty(), "Продолжительность фильма должна быть положительным числом");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().contains("duration")));
    }
}
