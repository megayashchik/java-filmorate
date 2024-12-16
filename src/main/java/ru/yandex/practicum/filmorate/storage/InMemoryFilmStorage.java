package ru.yandex.practicum.filmorate.storage;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Добавление фильма {}", film.getName());
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.trace("Фильм {} добавлен", film.getName());

        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
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

    @Override
    public void deleteFilm(Film filmId) {
        if (films.containsKey(filmId)) {
            films.remove(filmId);
        }
    }

    @Override
    public Optional<Film> findFilm(Integer filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

    @Override
    public Collection<Film> findAllFilms() {
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
