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
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film findFilm(Integer filmId) {
        return filmStorage.findFilm(filmId).orElseThrow(() -> new NotFoundException("Фильм с id = "
                + filmId + " не найден."));
    }

    public void addLike(Integer filmId, Integer userId) {
        Optional<User> user = userStorage.findUser(userId);
        Film film = findFilm(filmId);

        if (film.getLikes().contains(userId)) {
            throw new ValidateException("Пользователь " + user.get().getName() + " уже поставил лайк фильму "
                    + film.getName());
        }

        film.getLikes().add(userId);
        filmStorage.updateFilm(film);

        log.debug("Фильму " + film.getName() + " пользователь " + user.get().getName() + " поставил лайк.");
    }

    public void deleteLike(Integer filmId, Integer userId) {
        Optional<User> user = userStorage.findUser(userId);
        Film film = findFilm(filmId);

        if (!film.getLikes().contains(userId)) {
            throw new ValidateException("Пользователь " + user.get().getName() + " не ставил лайк фильму "
                    + film.getName());
        }

        film.getLikes().remove(userId);
        filmStorage.updateFilm(film);

        log.debug("Пользователь " + user.get().getName() + " удалил лайк к фильму " + film.getName());
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
            throw new ValidateException("Фильм с id= " + newFilm.getId() + " не найден.");
        }

        return filmStorage.updateFilm(newFilm);
    }

    public Collection<Film> findAllFilms() {
        log.debug("Получение списка фильмов.");

        return filmStorage.findAllFilms();
    }
}
