package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public void addLike(Integer filmId, Integer userId) {
        User user = userStorage.findUser(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = "
                + userId + " не найден"));
        Film film = filmStorage.findFilm(filmId).orElseThrow(() -> new NotFoundException("Фильм с id = "
                + filmId + " не найден."));

        if (film.getLikes().contains(userId)) {
            throw new ValidateException("Пользователь " + user.getName() + " уже поставил лайк фильму "
                    + film.getName());
        }

        log.debug("Фильму " + film.getName() + " пользователь " + user.getName() + " поставил лайк.");

        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        User user = userStorage.findUser(userId).orElseThrow(() -> new NotFoundException("Пользователь с id = "
                + userId + " не найден"));
        Film film = filmStorage.findFilm(filmId).orElseThrow(() -> new NotFoundException("Фильм с id = "
                + filmId + " не найден."));

        if (!film.getLikes().contains(userId)) {
            throw new ValidateException("Пользователь " + user.getName() + " не ставил лайк фильму "
                    + film.getName());
        }

        log.debug("Пользователь " + user.getName() + " удалил лайк к фильму " + film.getName());

        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);
    }

    public Collection<Film> findMostLikedFilms(Integer count) {
        log.debug("Получение " + count + " самых популярных фильмов.");

        return filmStorage.findAllFilms().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film createFilm(Film film) {
        log.debug("Добавление фильма " + film.getName());

        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film newFilm) {
        log.debug("Обновление данных фильма " + newFilm.getName());

        if (newFilm.getId() == null) {
            throw new NotFoundException("Фильм с id= " + newFilm.getId() + " не найден.");
        }

        return filmStorage.updateFilm(newFilm);
    }

    public Collection<Film> findAllFilms() {
        log.debug("Получение списка фильмов.");

        return filmStorage.findAllFilms();
    }
}
