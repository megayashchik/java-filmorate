package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.RatingStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class RatingDbStorage extends BaseStorage<Rating> implements RatingStorage {

    @Autowired
    public RatingDbStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, (rs, rowNum) -> {
            Rating rating = new Rating();
            rating.setId(rs.getInt("rating_id"));
            rating.setName(rs.getString("name"));

            return rating;
        }, Rating.class);
    }

    @Override
    public Optional<Rating> getById(Integer id) {
        String sql = "SELECT * FROM ratings WHERE rating_id = ?";
        Optional<Rating> result = findOne(sql, id);

        return result;
    }

    @Override
    public List<Rating> getAll() {
        String sql = "SELECT * FROM ratings";

        return findMany(sql);
    }
}