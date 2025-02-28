package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.mapper.RatingMapper;
import ru.yandex.practicum.filmorate.storage.interfaces.RatingStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatingService {
    private final RatingStorage ratingStorage;

    @Autowired
    public RatingService(RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    public RatingDto getRatingById(Integer id) {
        return ratingStorage.getById(id)
                .map(RatingMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Рейтинг не найден"));
    }

    public List<RatingDto> getAllRatings() {
        return ratingStorage.getAll().stream()
                .map(RatingMapper::toDto)
                .collect(Collectors.toList());
    }
}