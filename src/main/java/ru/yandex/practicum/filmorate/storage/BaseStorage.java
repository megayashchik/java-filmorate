package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public abstract class BaseStorage<T> {
    protected final JdbcTemplate jdbcTemplate;
    private final RowMapper<T> mapper;

    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbcTemplate.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {

            return Optional.empty();
        }
    }

    protected List<T> findMany(String query, Object... params) {
        try {
            return jdbcTemplate.query(query, mapper, params);
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка при получении списка записей " + e.getMessage(), e);
        }
    }

    protected boolean delete(String query, Object... params) {
        try {
            int rowsDeleted = jdbcTemplate.update(query, params);
            return rowsDeleted > 0;
        } catch (DataAccessException e) {
            throw new RuntimeException("Ошибка при удалении записи: " + e.getMessage(), e);
        }
    }

    protected void update(String query, Object... params) {
        try {
            int rowsUpdated = jdbcTemplate.update(query, params);
            if (rowsUpdated == 0) {
                throw new NotFoundException("Запись для обновления не найдена");
            }
        } catch (NotFoundException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new RuntimeException("Ошибка при обновлении данных: " + e.getMessage(), e);
        }
    }

    protected Integer insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }

            return ps;
        }, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);
        if (id != null) {

            return id.intValue();
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }
}
