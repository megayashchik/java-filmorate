package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;


@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private HashMap<Integer, User> users = new HashMap<>();


    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Создание пользователя {}", user.getName());

        checkName(user);

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.trace("Пользователь создан {}", user.getName());

        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User newUser) {
        if (newUser.getId() == null) {
            log.error("Не указан id {}", newUser.getName());
            throw new ValidateException("Id должен быть указан");
        }

        checkName(newUser);

        if (!users.containsKey(newUser.getId())) {
            log.error("Пользователь с id = {} не найден", newUser.getId());
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        }

        users.put(newUser.getId(), newUser);
        log.trace("Данные пользователя обновлены {}", newUser.getName());

        return newUser;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Получение пользователей {}", users.size());
        return users.values();
    }

    private User checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Имя не указано, устанавливаем имя на логин {}", user.getLogin());
            user.setName(user.getLogin());
        }

        return user;
    }

    private Integer getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }
}
