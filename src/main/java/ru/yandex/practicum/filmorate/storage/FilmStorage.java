package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;


public interface FilmStorage {

    Film createFilm(Film film);

    Film updateFilm(Film newFilm);

    void deleteFilm(Integer filmId);

    Optional<Film> findFilm(Integer filmId);

    Collection<Film> findAllFilms();
}
