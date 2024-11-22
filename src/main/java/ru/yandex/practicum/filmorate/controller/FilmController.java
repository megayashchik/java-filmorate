package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;


@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private HashMap<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Добавление фильма {}", film.getName());
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.trace("Фильм {} добавлен", film.getName());

        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film newFilm) {
        log.info("Обновление фильма {}", newFilm.getName());
        if (newFilm.getId() == null) {
            log.error("Не указан id {}", newFilm.getName());
            throw new ValidateException("Id должен быть указан");
        }

        if (films.containsKey(newFilm.getId())) {
            films.put(newFilm.getId(), newFilm);
            log.trace("Фильм обновлён {}", newFilm.getName());

            return newFilm;
        }

        log.error("Фильм " + newFilm.getName() + " не найден");
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }
}

