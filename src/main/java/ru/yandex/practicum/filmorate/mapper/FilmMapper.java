package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public class FilmMapper {
    public static FilmDto toDto(Film film, List<GenreDto> genres) {
        FilmDto dto = new FilmDto();
        dto.setId(film.getId());
        dto.setName(film.getName());
        dto.setDescription(film.getDescription());
        dto.setReleaseDate(film.getReleaseDate());
        dto.setDuration(film.getDuration());

        if (film.getMpa() != null) {
            dto.setMpa(RatingMapper.toDto(film.getMpa()));
        }

        dto.setGenres(genres);
        dto.setLikes(film.getLikes());

        return dto;
    }

    public static Film toEntity(FilmDto dto) {
        Film film = new Film();
        film.setId(dto.getId());
        film.setName(dto.getName());
        film.setDescription(dto.getDescription());
        film.setReleaseDate(dto.getReleaseDate());
        film.setDuration(dto.getDuration());

        if (dto.getMpa() != null) {
            film.setMpa(RatingMapper.toEntity(dto.getMpa()));
        }

        film.getLikes().addAll(dto.getLikes());

        return film;
    }
}

