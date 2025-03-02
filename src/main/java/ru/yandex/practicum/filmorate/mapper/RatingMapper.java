package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.model.Rating;

public class RatingMapper {
    public static RatingDto toDto(Rating rating) {
        RatingDto dto = new RatingDto();
        dto.setId(rating.getId());
        dto.setName(rating.getName());

        return dto;
    }

    public static Rating toEntity(RatingDto dto) {
        Rating rating = new Rating();
        rating.setId(dto.getId());
        rating.setName(dto.getName());

        return rating;
    }
}
