package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.impl.RatingDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({RatingDbStorage.class})
class RatingDbStorageTests {

    private final RatingDbStorage ratingStorage;

    @Test
    void testGetById() {
        Optional<Rating> ratingOptional = ratingStorage.getById(1);

        assertThat(ratingOptional)
                .isPresent()
                .hasValueSatisfying(rating ->
                        assertThat(rating).hasFieldOrPropertyWithValue("id", 1)
                                .hasFieldOrPropertyWithValue("name", "G")
                );
    }

    @Test
    void testGetAll() {
        List<Rating> ratings = ratingStorage.getAll();

        assertThat(ratings).hasSize(5)
                .anySatisfy(rating -> assertThat(rating).hasFieldOrPropertyWithValue("id", 1))
                .anySatisfy(rating -> assertThat(rating).hasFieldOrPropertyWithValue("id", 2));
    }
}
