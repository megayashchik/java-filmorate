package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public GenreDto getGenreById(Integer id) {
        return genreStorage.getById(id)
                .map(GenreMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Жанр с id=" + id + " не найден"));
    }

    public List<GenreDto> getAllGenres() {
        return genreStorage.getAll().stream()
                .map(GenreMapper::toDto)
                .collect(Collectors.toList());
    }
}
