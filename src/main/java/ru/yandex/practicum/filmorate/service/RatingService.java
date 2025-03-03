package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.RatingMapper;
import ru.yandex.practicum.filmorate.storage.interfaces.RatingStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RatingService {
    private final RatingStorage ratingStorage;

    @Autowired
    public RatingService(RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    public RatingDto getRatingById(Integer id) {
        log.info("Получение рейтинга с id: {}", id);
        RatingDto result = ratingStorage.getById(id)
                .map(RatingMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("Рейтинг с  id={} не найден", id);
                    return new NotFoundException("Рейтинг с id=" + id + " не найден");
                });
        log.info("Полученный рейтинг: {}", result);

        return result;
    }

    public List<RatingDto> getAllRatings() {
        log.info("Извлечение всех рейтингов");
        List<RatingDto> result = ratingStorage.getAll().stream()
                .map(RatingMapper::toDto)
                .collect(Collectors.toList());
        log.info("Получено {} рейтингов: {}", result.size(), result);

        return result;
    }
}