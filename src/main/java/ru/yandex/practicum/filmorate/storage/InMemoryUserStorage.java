package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidateException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        log.info("Создание пользователя {}", user.getName());

        checkName(user);

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.trace("Пользователь создан {}", user.getName());

        return user;
    }

    @Override
    public User updateUser(User newUser) {
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

    @Override
    public void deleteUser(Integer userId) {
        if (users.containsKey(userId)) {
            users.remove(userId);
        }
    }

    @Override
    public Optional<User> findUser(Integer userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public Collection<User> findAllUsers() {
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
