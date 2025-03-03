package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
public class RatingController {
    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping
    public ResponseEntity<List<RatingDto>> getAllRatings() {
        List<RatingDto> ratings = ratingService.getAllRatings();

        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingDto> getRatingById(@PathVariable Integer id) {
        RatingDto ratingDto = ratingService.getRatingById(id);

        return ResponseEntity.ok(ratingDto);
    }
}
