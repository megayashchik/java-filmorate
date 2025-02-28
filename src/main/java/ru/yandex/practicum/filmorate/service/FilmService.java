package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, GenreStorage genreStorage) {
        this.filmStorage = filmStorage;
        this.genreStorage = genreStorage;
    }

    public FilmDto createFilm(FilmDto filmDto) {
        Film film = FilmMapper.toEntity(filmDto);
        film = filmStorage.createFilm(film);
        final Integer filmId = film.getId();
        if (filmDto.getGenres() != null) {
            filmDto.getGenres().forEach(genreDto -> filmStorage.addGenreId(filmId, genreDto.getId()));
        }
        List<Genre> genres = genreStorage.findGenresByFilmId(film.getId());
        List<GenreDto> genreDtos = genres.stream()
                .map(GenreMapper::toDto)
                .collect(Collectors.toList());

        return FilmMapper.toDto(film, genreDtos);
    }

    public FilmDto updateFilm(FilmDto filmDto) {
        Film film = filmStorage.findFilmById(filmDto.getId())
                .orElseThrow(() -> new NoSuchElementException("Фильм не найден"));
        film.setName(filmDto.getName());
        film.setDescription(filmDto.getDescription());
        film.setReleaseDate(filmDto.getReleaseDate());
        film.setDuration(filmDto.getDuration());
        if (filmDto.getMpa() != null) {
            Rating rating = new Rating();
            rating.setId(filmDto.getMpa().getId());
            film.setMpa(rating);
        }
        film = filmStorage.updateFilm(film);

        filmStorage.deleteGenresByFilmId(film.getId());

        final Integer filmId = film.getId();
        if (filmDto.getGenres() != null) {
            filmDto.getGenres().forEach(genreDto -> filmStorage.addGenreId(filmId, genreDto.getId()));
        }

        List<Genre> genres = genreStorage.findGenresByFilmId(film.getId());
        List<GenreDto> genreDtos = genres.stream()
                .map(GenreMapper::toDto)
                .collect(Collectors.toList());

        return FilmMapper.toDto(film, genreDtos);
    }

    public void deleteFilm(Integer filmId) {
        filmStorage.deleteFilm(filmId);
    }

    public FilmDto getFilmById(Integer filmId) {
        Film film = filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NoSuchElementException("Фильм не найден"));
        List<Genre> genres = genreStorage.findGenresByFilmId(filmId);
        List<GenreDto> genreDtos = genres.stream().map(GenreMapper::toDto).collect(Collectors.toList());

        return FilmMapper.toDto(film, genreDtos);
    }

    public Collection<FilmDto> getAllFilms() {
        return filmStorage.findAllFilms().stream().map(film -> {
            List<Genre> genres = genreStorage.findGenresByFilmId(film.getId());
            List<GenreDto> genreDtos = genres.stream().map(GenreMapper::toDto).collect(Collectors.toList());
            return FilmMapper.toDto(film, genreDtos);
        }).collect(Collectors.toList());
    }

    public void addLike(Integer filmId, Integer userId) {
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        filmStorage.deleteLike(filmId, userId);
    }

    public List<FilmDto> getMostLikedFilms(int limit) {
        return filmStorage.findMostLikedFilms(limit).stream().map(film -> {
            List<Genre> genres = genreStorage.findGenresByFilmId(film.getId());
            List<GenreDto> genreDtos = genres.stream().map(GenreMapper::toDto).collect(Collectors.toList());
            return FilmMapper.toDto(film, genreDtos);
        }).collect(Collectors.toList());
    }
}


