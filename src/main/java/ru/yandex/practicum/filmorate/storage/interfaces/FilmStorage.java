package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    boolean deleteFilm(Integer filmId);

    Optional<Film> findFilmById(Integer filmId);

    Collection<Film> findAllFilms();

    void addLike(Integer filmId, Integer userId);

    List<Integer> getLikesByFilmId(Integer filmId);

    List<Film> findMostLikedFilms(int limit);

    boolean deleteLike(Integer filmId, Integer userId);

    void addGenreId(Integer filmId, Integer genreId);

    void deleteGenresByFilmId(Integer filmId);

    Integer findRatingId(String ratingName);

    List<Integer> findGenresIds(Integer filmId);
}
