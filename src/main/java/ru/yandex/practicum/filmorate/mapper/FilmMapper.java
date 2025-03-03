package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.stream.Collectors;

public class FilmMapper {
    public static Film toEntity(FilmDto filmDto) {
        Film film = new Film();
        film.setId(filmDto.getId());
        film.setName(filmDto.getName());
        film.setDescription(filmDto.getDescription());
        film.setReleaseDate(filmDto.getReleaseDate());
        film.setDuration(filmDto.getDuration());

        if (filmDto.getMpa() != null) {
            film.setMpa(RatingMapper.toEntity(filmDto.getMpa()));
        }

        if (filmDto.getGenres() != null) {
            film.setGenres(filmDto.getGenres().stream()
                    .map(GenreMapper::toEntity)
                    .collect(Collectors.toList()));
        }

        return film;
    }

    public static FilmDto toDto(Film film, List<GenreDto> genreDtos) {
        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDescription(film.getDescription());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setDuration(film.getDuration());

        if (film.getMpa() != null) {
            filmDto.setMpa(RatingMapper.toDto(film.getMpa()));
        }

        filmDto.setGenres(genreDtos);
        filmDto.setLikes(film.getLikes());

        return filmDto;
    }
}