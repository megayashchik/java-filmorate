package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class})
class FilmDbStorageTests {

    private final FilmDbStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;

    private Film testFilm1;
    private Film testFilm2;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM likes");
        jdbcTemplate.execute("DELETE FROM film_genres");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1");
        jdbcTemplate.execute("ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1");

        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "user1@example.com", "login1", "User1", LocalDate.of(2000, 10, 11));
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                "user2@example.com", "login2", "User2", LocalDate.of(1980, 8, 20));

        testFilm1 = createTestFilm("Film 1", "Description 1",
                LocalDate.of(2005, 7, 8), 120, 1); // G
        testFilm2 = createTestFilm("Film 2", "Description 2",
                LocalDate.of(1960, 11, 2), 150, 2); // PG

        filmStorage.addGenreId(1, 1); // Комедия
        filmStorage.addGenreId(1, 2); // Драма
        filmStorage.addGenreId(2, 3); // Мультфильм

        filmStorage.addLike(1, 1);
        filmStorage.addLike(1, 2);
        filmStorage.addLike(2, 1);
    }

    private Film createTestFilm(String name, String description, LocalDate releaseDate, int duration, int ratingId) {
        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        Rating rating = new Rating();
        rating.setId(ratingId);
        film.setMpa(rating);
        return filmStorage.createFilm(film);
    }

    @Test
    void testFindFilmById() {
        Optional<Film> filmOptional = filmStorage.findFilmById(1);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "Film 1")
                );
    }

    @Test
    void testFindAllFilms() {
        List<Film> films = (List<Film>) filmStorage.findAllFilms();

        assertThat(films).hasSize(2)
                .anySatisfy(film -> assertThat(film).hasFieldOrPropertyWithValue("id", 1))
                .anySatisfy(film -> assertThat(film).hasFieldOrPropertyWithValue("id", 2));
    }

    @Test
    void testCreateFilm() {
        Film newFilm = new Film();
        newFilm.setName("Film 3");
        newFilm.setDescription("Description 3");
        newFilm.setReleaseDate(LocalDate.of(2022, 3, 3));
        newFilm.setDuration(90);
        Rating rating = new Rating();
        rating.setId(3); // PG-13
        newFilm.setMpa(rating);

        Film createdFilm = filmStorage.createFilm(newFilm);

        assertThat(createdFilm)
                .hasFieldOrPropertyWithValue("id", 3)
                .hasFieldOrPropertyWithValue("name", "Film 3");
    }

    @Test
    void testUpdateFilm() {
        Film filmToUpdate = new Film();
        filmToUpdate.setId(1);
        filmToUpdate.setName("Updated Film");
        filmToUpdate.setDescription("Updated Description");
        filmToUpdate.setReleaseDate(LocalDate.of(2020, 2, 2));
        filmToUpdate.setDuration(130);
        Rating rating = new Rating();
        rating.setId(2); // PG
        filmToUpdate.setMpa(rating);

        Film updatedFilm = filmStorage.updateFilm(filmToUpdate);

        assertThat(updatedFilm)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "Updated Film");
    }

    @Test
    void testDeleteFilm() {
        filmStorage.deleteGenresByFilmId(1);
        jdbcTemplate.update("DELETE FROM likes WHERE film_id = ?", 1);

        boolean deleted = filmStorage.deleteFilm(1);

        assertThat(deleted).isTrue();

        Optional<Film> filmOptional = filmStorage.findFilmById(1);
        assertThat(filmOptional).isEmpty();
    }

    @Test
    void testAddLike() {
        filmStorage.addLike(2, 2);

        List<Integer> likes = filmStorage.getLikesByFilmId(2);
        assertThat(likes).hasSize(2).contains(1, 2);
    }

    @Test
    void testGetLikesByFilmId() {
        List<Integer> likes = filmStorage.getLikesByFilmId(1);

        assertThat(likes).hasSize(2).contains(1, 2);
    }

    @Test
    void testFindMostLikedFilms() {
        List<Film> mostLiked = filmStorage.findMostLikedFilms(2);

        assertThat(mostLiked).hasSize(2)
                .satisfies(films -> {
                    assertThat(films.get(0)).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(films.get(1)).hasFieldOrPropertyWithValue("id", 2);
                });
    }

    @Test
    void testDeleteLike() {
        boolean deleted = filmStorage.deleteLike(1, 1);

        assertThat(deleted).isTrue();

        List<Integer> likes = filmStorage.getLikesByFilmId(1);
        assertThat(likes).hasSize(1).contains(2);
    }

    @Test
    void testAddGenreId() {
        filmStorage.addGenreId(2, 4); // Триллер

        List<Integer> genreIds = filmStorage.findGenresIds(2);
        assertThat(genreIds).hasSize(2).contains(3, 4);
    }

    @Test
    void testDeleteGenresByFilmId() {
        filmStorage.deleteGenresByFilmId(1);

        List<Integer> genreIds = filmStorage.findGenresIds(1);
        assertThat(genreIds).isEmpty();
    }

    @Test
    void testFindRatingId() {
        Integer ratingId = filmStorage.findRatingId("G");

        assertThat(ratingId).isEqualTo(1);
    }

    @Test
    void testFindGenresIds() {
        List<Integer> genreIds = filmStorage.findGenresIds(1);

        assertThat(genreIds).hasSize(2).contains(1, 2);
    }
}





