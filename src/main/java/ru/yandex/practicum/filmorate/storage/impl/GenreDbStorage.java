package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FilmGenre;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class GenreDbStorage extends BaseStorage<Genre> implements GenreStorage {

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, (rs, rowNum) -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("name")
        ));
    }

    @Override
    public Optional<Genre> getById(Integer id) {
        String sql = "SELECT * FROM genres WHERE genre_id = ?";

        return findOne(sql, id);
    }

    @Override
    public List<Genre> getAll() {
        String sql = "SELECT * FROM genres";

        return findMany(sql);
    }

    @Override
    public List<Genre> findGenresByFilmId(Integer filmId) {
        String sql = "SELECT g.* FROM genres g " +
                "JOIN film_genres fg ON g.genre_id = fg.genre_id WHERE fg.film_id = ?";

        return findMany(sql, filmId);
    }

    @Override
    public List<Genre> getByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        String sql = "SELECT * FROM genres WHERE genre_id IN (" +
                String.join(",", Collections.nCopies(ids.size(), "?")) + ")";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(
                rs.getInt("genre_id"),
                rs.getString("name")
        ), ids.toArray());
    }

    @Override
    public List<FilmGenre> findAllFilmGenres() {
        String sql = "SELECT fg.film_id, g.genre_id, g.name " +
                "FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.genre_id";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new FilmGenre(
                rs.getInt("film_id"),
                new Genre(rs.getInt("genre_id"), rs.getString("name"))
        ));
    }

    @Override
    public List<FilmGenre> findFilmGenresByFilmIds(List<Integer> filmIds) {
        if (filmIds == null || filmIds.isEmpty()) {

            return Collections.emptyList();
        }
        String sql = "SELECT fg.film_id, g.genre_id, g.name " +
                "FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id IN (" + String.join(",", Collections.nCopies(filmIds.size(), "?")) + ")";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new FilmGenre(
                rs.getInt("film_id"),
                new Genre(rs.getInt("genre_id"), rs.getString("name"))
        ), filmIds.toArray());
    }
}


