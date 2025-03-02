package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    Optional<Genre> getById(Integer id);

    List<Genre> getAll();

    List<Genre> findGenresByFilmId(Integer filmId);

    List<Genre> getByIds(List<Integer> ids); // Добавлено из #4
}
