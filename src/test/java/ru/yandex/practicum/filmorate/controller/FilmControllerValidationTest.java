package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.RatingDto;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerValidationTest {

    private Validator validator;
    private FilmDto filmDto;

    @BeforeEach
    public void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
        filmDto = new FilmDto();
    }

    @Test
    public void create_Valid_Film() {
        filmDto.setName("Название фильма");
        filmDto.setDescription("Описание фильма");
        filmDto.setReleaseDate(LocalDate.of(2024, 11, 20));
        filmDto.setDuration(120);
        RatingDto mpa = new RatingDto();
        filmDto.setMpa(mpa);
        List<GenreDto> genres = new ArrayList<>();
        GenreDto genre = new GenreDto();
        genres.add(genre);
        filmDto.setGenres(genres);

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(filmDto);

        assertEquals(0, violations.size());
        assertTrue(violations.isEmpty(), "Ошибка при заполнении полей класса");
    }

    @Test
    public void create_Film_With_Empty_Name() {
        filmDto.setName("");
        filmDto.setDescription("Описание фильма");
        filmDto.setReleaseDate(LocalDate.of(2024, 11, 20));
        filmDto.setDuration(120);
        RatingDto mpa = new RatingDto();
        filmDto.setMpa(mpa);
        List<GenreDto> genres = new ArrayList<>();
        GenreDto genre = new GenreDto();
        genres.add(genre);
        filmDto.setGenres(genres);

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(filmDto);

        assertEquals(1, violations.size());
        assertFalse(violations.isEmpty(), "Название фильма не может быть пустым");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    public void create_Film_With_Null_Name() {
        filmDto.setName(null);
        filmDto.setDescription("Описание фильма");
        filmDto.setReleaseDate(LocalDate.of(2024, 11, 20));
        filmDto.setDuration(120);
        RatingDto mpa = new RatingDto();
        filmDto.setMpa(mpa);
        List<GenreDto> genres = new ArrayList<>();
        GenreDto genre = new GenreDto();
        genres.add(genre);
        filmDto.setGenres(genres);

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(filmDto);

        assertEquals(1, violations.size());
        assertFalse(violations.isEmpty(), "Название фильма не может быть пустым");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("name")));
    }

    @Test
    public void create_Film_With_Null_Description() {
        filmDto.setName("Название фильма");
        filmDto.setDescription(null);
        filmDto.setReleaseDate(LocalDate.of(2024, 11, 20));
        filmDto.setDuration(120);
        RatingDto mpa = new RatingDto();
        filmDto.setMpa(mpa);
        List<GenreDto> genres = new ArrayList<>();
        GenreDto genre = new GenreDto();
        genres.add(genre);
        filmDto.setGenres(genres);

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(filmDto);

        assertEquals(1, violations.size());
        assertFalse(violations.isEmpty(), "Описание фильма не может быть null");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    public void create_Film_With_Long_Description() {
        filmDto.setName("Название фильма");
        filmDto.setDescription("Описание фильма".repeat(20));
        filmDto.setReleaseDate(LocalDate.of(2024, 11, 20));
        filmDto.setDuration(120);
        RatingDto mpa = new RatingDto();
        filmDto.setMpa(mpa);
        List<GenreDto> genres = new ArrayList<>();
        GenreDto genre = new GenreDto();
        genres.add(genre);
        filmDto.setGenres(genres);

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(filmDto);

        assertEquals(1, violations.size());
        assertFalse(violations.isEmpty(),
                "Максимальная длина описания фильма не должна превышать 200 символов");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("description")));
    }

    @Test
    public void create_Film_With_Null_ReleaseDate() {
        filmDto.setName("Название фильма");
        filmDto.setDescription("Описание фильма");
        filmDto.setReleaseDate(null);
        filmDto.setDuration(120);
        RatingDto mpa = new RatingDto();
        filmDto.setMpa(mpa);
        List<GenreDto> genres = new ArrayList<>();
        GenreDto genre = new GenreDto();
        genres.add(genre);
        filmDto.setGenres(genres);

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(filmDto);

        assertEquals(1, violations.size());
        assertFalse(violations.isEmpty(), "Дата релиза не может быть null");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")));
    }

    @Test
    public void create_Film_Before_Date_Release() {
        filmDto.setName("Название фильма");
        filmDto.setDescription("Описание фильма");
        filmDto.setReleaseDate(LocalDate.of(1894, 12, 28));
        filmDto.setDuration(120);
        RatingDto mpa = new RatingDto();
        filmDto.setMpa(mpa);
        List<GenreDto> genres = new ArrayList<>();
        GenreDto genre = new GenreDto();
        genres.add(genre);
        filmDto.setGenres(genres);

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(filmDto);

        assertEquals(1, violations.size());
        assertFalse(violations.isEmpty(), "Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("releaseDate")));
    }

    @Test
    public void create_Film_With_Same_Date_Release() {
        filmDto.setName("Название фильма");
        filmDto.setDescription("Описание фильма");
        filmDto.setReleaseDate(LocalDate.of(1895, 12, 28));
        filmDto.setDuration(120);
        RatingDto mpa = new RatingDto();
        filmDto.setMpa(mpa);
        List<GenreDto> genres = new ArrayList<>();
        GenreDto genre = new GenreDto();
        genres.add(genre);
        filmDto.setGenres(genres);

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(filmDto);

        assertEquals(0, violations.size());
        assertTrue(violations.isEmpty(), "Дата релиза фильма может быть 28 декабря 1895 года");
    }

    @Test
    public void create_Film_With_Null_Duration() {
        filmDto.setName("Название фильма");
        filmDto.setDescription("Описание фильма");
        filmDto.setReleaseDate(LocalDate.of(2024, 11, 20));
        filmDto.setDuration(null);
        RatingDto mpa = new RatingDto();
        filmDto.setMpa(mpa);
        List<GenreDto> genres = new ArrayList<>();
        GenreDto genre = new GenreDto();
        genres.add(genre);
        filmDto.setGenres(genres);

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(filmDto);

        assertEquals(1, violations.size());
        assertFalse(violations.isEmpty(), "Продолжительность фильма не может быть null");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("duration")));
    }

    @Test
    public void create_Film_With_Negative_Duration() {
        filmDto.setName("Название фильма");
        filmDto.setDescription("Описание фильма");
        filmDto.setReleaseDate(LocalDate.of(2024, 11, 20));
        filmDto.setDuration(-1);
        RatingDto mpa = new RatingDto();
        filmDto.setMpa(mpa);
        List<GenreDto> genres = new ArrayList<>();
        GenreDto genre = new GenreDto();
        genres.add(genre);
        filmDto.setGenres(genres);

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(filmDto);

        assertEquals(1, violations.size());
        assertFalse(violations.isEmpty(), "Продолжительность фильма должна быть положительным числом");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("duration")));
    }

    @Test
    public void create_Film_With_Zero_Duration() {
        filmDto.setName("Название фильма");
        filmDto.setDescription("Описание фильма");
        filmDto.setReleaseDate(LocalDate.of(2024, 11, 20));
        filmDto.setDuration(0);
        RatingDto mpa = new RatingDto();
        filmDto.setMpa(mpa);
        List<GenreDto> genres = new ArrayList<>();
        GenreDto genre = new GenreDto();
        genres.add(genre);
        filmDto.setGenres(genres);

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(filmDto);

        assertEquals(1, violations.size());
        assertFalse(violations.isEmpty(), "Продолжительность фильма должна быть положительным числом");
        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("duration")));
    }

    @Test
    public void create_Film_With_Null_Mpa() {
        filmDto.setName("Название фильма");
        filmDto.setDescription("Описание фильма");
        filmDto.setReleaseDate(LocalDate.of(2024, 11, 20));
        filmDto.setDuration(120);
        filmDto.setMpa(null);
        filmDto.setGenres(new ArrayList<>());

        Set<ConstraintViolation<FilmDto>> violations = validator.validate(filmDto);

        assertEquals(0, violations.size());
        assertTrue(violations.isEmpty(), "Рейтинг MPA может быть null");
    }
}
