package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

public class GenreMapper {
    public static GenreDto toDto(Genre genre) {
        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setName(genre.getName());
        return dto;
    }

    public static Genre toEntity(GenreDto dto) {
        Genre genre = new Genre();
        genre.setId(dto.getId());
        genre.setName(dto.getName());
        return genre;
    }
}
