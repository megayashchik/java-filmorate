package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.RatingStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class FilmDbStorage extends BaseStorage<Film> implements FilmStorage {

    private final GenreStorage genreStorage;
    private final RatingStorage ratingStorage;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate, GenreStorage genreStorage, RatingStorage ratingStorage) {
        super(jdbcTemplate, FilmDbStorage::mapRowToFilm, Film.class);
        this.genreStorage = genreStorage;
        this.ratingStorage = ratingStorage;
    }

    private static Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        Rating rating = new Rating();
        rating.setId(rs.getInt("rating_id"));
        rating.setName(rs.getString("rating_name"));
        film.setMpa(rating);

        return film;
    }

    @Override
    public Film createFilm(Film film) {
        String sqlQuery = "INSERT INTO films (name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        Integer id = insert(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null);
        film.setId(id);

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String genreSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(genreSql, film.getGenres().stream()
                    .filter(genre -> genre != null && genre.getId() != null)
                    .map(genre -> new Object[]{id, genre.getId()})
                    .collect(Collectors.toList()));
        }

        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "WHERE film_id = ?";
        update(sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().getId() : null,
                film.getId());

        deleteGenresByFilmId(film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            String genreSql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(genreSql, film.getGenres().stream()
                    .filter(genre -> genre != null && genre.getId() != null)
                    .map(genre -> new Object[]{film.getId(), genre.getId()})
                    .collect(Collectors.toList()));
        }

        return film;
    }

    @Override
    public boolean deleteFilm(Integer filmId) {
        String sqlQuery = "DELETE FROM films WHERE film_id = ?";

        return delete(sqlQuery, filmId);
    }

    @Override
    public Optional<Film> findFilmById(Integer filmId) {
        String sqlQuery = "SELECT f.*, r.name AS rating_name " +
                "FROM films f LEFT JOIN ratings r ON f.rating_id = r.rating_id " +
                "WHERE f.film_id = ?";
        Optional<Film> film = findOne(sqlQuery, filmId);
        film.ifPresent(f -> f.setGenres(genreStorage.findGenresByFilmId(filmId)));

        return film;
    }

    @Override
    public Collection<Film> findAllFilms() {
        String sqlQuery = "SELECT f.*, r.name AS rating_name " +
                "FROM films f LEFT JOIN ratings r ON f.rating_id = r.rating_id";
        Collection<Film> films = findMany(sqlQuery);
        films.forEach(film -> film.setGenres(genreStorage.findGenresByFilmId(film.getId())));

        return films;
    }

    @Override
    public void addLike(Integer filmId, Integer userId) {
        String sqlQuery = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sqlQuery, filmId, userId);
    }

    @Override
    public List<Integer> getLikesByFilmId(Integer filmId) {
        String sqlQuery = "SELECT user_id FROM likes WHERE film_id = ?";

        return jdbcTemplate.queryForList(sqlQuery, Integer.class, filmId);
    }

    @Override
    public List<Film> findMostLikedFilms(int limit) {
        String sqlQuery = "SELECT f.*, r.name AS rating_name, COUNT(l.user_id) AS like_count " +
                "FROM films f " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "LEFT JOIN ratings r ON f.rating_id = r.rating_id " +
                "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, f.rating_id, r.name " +
                "ORDER BY like_count DESC " +
                "LIMIT ?";
        List<Film> films = jdbcTemplate.query(sqlQuery, FilmDbStorage::mapRowToFilm, limit);
        films.forEach(film -> film.setGenres(genreStorage.findGenresByFilmId(film.getId())));

        return films;
    }

    @Override
    public boolean deleteLike(Integer filmId, Integer userId) {
        String sqlQuery = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";

        return delete(sqlQuery, filmId, userId);
    }

    @Override
    public void addGenreId(Integer filmId, Integer genreId) {
        if (genreId != null) {
            String sqlQuery = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.update(sqlQuery, filmId, genreId);
        }
    }

    @Override
    public void deleteGenresByFilmId(Integer filmId) {
        String sqlQuery = "DELETE FROM film_genres WHERE film_id = ?";
        jdbcTemplate.update(sqlQuery, filmId);
    }

    @Override
    public Integer findRatingId(String ratingName) {
        String sqlQuery = "SELECT rating_id FROM ratings WHERE name = ?";

        return jdbcTemplate.queryForObject(sqlQuery, Integer.class, ratingName);
    }

    @Override
    public List<Integer> findGenresIds(Integer filmId) {
        String sqlQuery = "SELECT genre_id FROM film_genres WHERE film_id = ?";

        return jdbcTemplate.queryForList(sqlQuery, Integer.class, filmId);
    }
}



























