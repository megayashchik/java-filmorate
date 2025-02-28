package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public ResponseEntity<FilmDto> createFilm(@Valid @RequestBody FilmDto filmDto) {
        FilmDto created = filmService.createFilm(filmDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FilmDto> updateFilm(@PathVariable Integer id, @RequestBody FilmDto filmDto) {
        filmDto.setId(id);
        FilmDto updated = filmService.updateFilm(filmDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFilm(@PathVariable Integer id) {
        filmService.deleteFilm(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilmDto> getFilmById(@PathVariable Integer id) {
        FilmDto filmDto = filmService.getFilmById(id);
        return ResponseEntity.ok(filmDto);
    }

    @GetMapping
    public ResponseEntity<Collection<FilmDto>> getAllFilms() {
        Collection<FilmDto> films = filmService.getAllFilms();
        return ResponseEntity.ok(films);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.addLike(id, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> deleteLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.deleteLike(id, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/popular")
    public ResponseEntity<List<FilmDto>> getMostLikedFilms(@RequestParam(defaultValue = "10") int count) {
        List<FilmDto> films = filmService.getMostLikedFilms(count);
        return ResponseEntity.ok(films);
    }
}