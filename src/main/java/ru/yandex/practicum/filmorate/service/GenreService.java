package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.interfaces.GenreStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GenreService {
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public GenreDto getGenreById(Integer id) {
        log.info("Извлечение жанра с id: {}", id);
        GenreDto result = genreStorage.getById(id)
                .map(GenreMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("Жанр с id={} не найден", id);

                    return new NotFoundException("Жанр с id=" + id + " не найден");
                });
        log.info("Полученный жанр: {}", result);

        return result;
    }

    public List<GenreDto> getAllGenres() {
        log.info("Получение всех жанров");
        List<GenreDto> result = genreStorage.getAll().stream()
                .map(GenreMapper::toDto)
                .collect(Collectors.toList());
        log.info("Получено {} жанров: {}", result.size(), result);

        return result;
    }
}
