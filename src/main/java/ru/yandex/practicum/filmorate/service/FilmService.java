package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.RatingStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final RatingStorage ratingStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, GenreStorage genreStorage, RatingStorage ratingStorage) {
        this.filmStorage = filmStorage;
        this.genreStorage = genreStorage;
        this.ratingStorage = ratingStorage;
    }

    public FilmDto create(FilmDto filmDto) {
        log.info("Создание фильма: {}", filmDto);
        Film film = FilmMapper.toEntity(filmDto);

        if (film.getMpa() != null && film.getMpa().getId() != null) {
            Integer mpaId = film.getMpa().getId();
            if (mpaId < 1 || mpaId > 5) {
                throw new NotFoundException("Рейтинг с id=" + mpaId + " не найден");
            }
            Rating mpa = ratingStorage.getById(mpaId)
                    .orElseThrow(() -> new NotFoundException("Рейтинг с id=" + mpaId + " не найден"));
            film.setMpa(mpa);
        }

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Integer> genreIds = film.getGenres().stream()
                    .map(Genre::getId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            List<Genre> genres = genreStorage.getByIds(genreIds);

            if (genres.size() != genreIds.size()) {
                throw new NotFoundException("Один или несколько жанров не найдены: " + genreIds);
            }
            film.setGenres(genres);
        }

        Film createdFilm = filmStorage.createFilm(film);

        filmStorage.deleteGenresByFilmId(createdFilm.getId());

        if (createdFilm.getGenres() != null && !createdFilm.getGenres().isEmpty()) {
            for (Genre genre : createdFilm.getGenres()) {
                try {
                    filmStorage.addGenreId(createdFilm.getId(), genre.getId());
                } catch (DataIntegrityViolationException e) {
                    log.warn("Жанр id={} уже существует для фильма id={}", genre.getId(), createdFilm.getId());
                }
            }
        }

        FilmDto result = FilmMapper.toDto(createdFilm, createdFilm.getGenres() != null ?
                createdFilm.getGenres().stream().map(GenreMapper::toDto).collect(Collectors.toList()) :
                Collections.emptyList());
        log.info("Фильм создан: {}", result);

        return result;
    }

    public FilmDto update(FilmDto filmDto) {
        log.info("Обновление фильма: {}", filmDto);
        if (filmDto.getId() == null) {
            throw new ValidationException("ID фильма обязателен для обновления");
        }
        Film existingFilm = filmStorage.findFilmById(filmDto.getId())
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + filmDto.getId() + " не найден"));

        Film film = FilmMapper.toEntity(filmDto);

        if (film.getMpa() != null && film.getMpa().getId() != null) {
            Integer mpaId = film.getMpa().getId();
            if (mpaId < 1 || mpaId > 5) {
                throw new NotFoundException("Рейтинг с id=" + mpaId + " не найден");
            }
            Rating mpa = ratingStorage.getById(mpaId)
                    .orElseThrow(() -> new NotFoundException("Рейтинг с id=" + mpaId + " не найден"));
            film.setMpa(mpa);
        }

        Film updated;
        try {
            updated = filmStorage.updateFilm(film);
        } catch (RuntimeException e) {
            throw new NotFoundException("Фильм с id=" + film.getId() + " не найден для обновления");
        }

        filmStorage.deleteGenresByFilmId(updated.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            List<Integer> genreIds = film.getGenres().stream()
                    .map(Genre::getId)
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            List<Genre> genres = genreStorage.getByIds(genreIds);

            if (genres.size() != genreIds.size()) {
                throw new NotFoundException("Один или несколько жанров не найдены: " + genreIds);
            }
            updated.setGenres(genres);

            for (Genre genre : genres) {
                try {
                    filmStorage.addGenreId(updated.getId(), genre.getId());
                } catch (DataIntegrityViolationException e) {
                    log.warn("Жанр id={} уже существует для фильма id={}", genre.getId(), updated.getId());
                }
            }
        }

        List<Genre> genres = genreStorage.findGenresByFilmId(updated.getId());
        FilmDto result = FilmMapper.toDto(updated, genres.stream()
                .map(GenreMapper::toDto)
                .collect(Collectors.toList()));
        log.info("Фильм обновлён: {}", result);

        return result;
    }

    public void deleteFilm(Integer filmId) {
        filmStorage.deleteFilm(filmId);
    }

    public FilmDto getFilmById(Integer filmId) {
        Film film = filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id=" + filmId + " не найден"));
        List<Genre> genres = genreStorage.findGenresByFilmId(filmId);

        return FilmMapper.toDto(film, genres.stream()
                .map(GenreMapper::toDto)
                .collect(Collectors.toList()));
    }

    public Collection<FilmDto> getAllFilms() {
        return filmStorage.findAllFilms().stream()
                .map(film -> FilmMapper.toDto(film, genreStorage.findGenresByFilmId(film.getId()).stream()
                        .map(GenreMapper::toDto)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    public void addLike(Integer filmId, Integer userId) {
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        filmStorage.deleteLike(filmId, userId);
    }

    public List<FilmDto> getMostLikedFilms(int limit) {
        return filmStorage.findMostLikedFilms(limit).stream()
                .map(film -> FilmMapper.toDto(film, genreStorage.findGenresByFilmId(film.getId()).stream()
                        .map(GenreMapper::toDto)
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }
}


