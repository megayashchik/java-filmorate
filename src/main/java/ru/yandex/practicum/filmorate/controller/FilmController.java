package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;


@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        log.info("Добавление фильма {}.", film.getName());
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.info("Обновление фильма {}.", newFilm.getName());
        return filmService.updateFilm(newFilm);
    }

    @GetMapping
    public Collection<Film> findAllFilms() {
        log.info("Список всех фильмов.");
        return filmService.findAllFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        log.info("Пользователь с id = {} поставил лайк к фильму id = {}.", userId, filmId);
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        log.info("Пользователь с id = {} удалил лайк к фильму id = {}.", userId, filmId);
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> findMostLikedFilms(@RequestParam(value = "count", defaultValue = "10") Integer count) {
        log.info("{} самых популярных фильмов.", count);
        return filmService.findMostLikedFilms(count);
    }
}

