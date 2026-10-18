package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController   //говорит Spring, что класс обрабатывает HTTP-запросы и возвращает данные, например JSON.
@RequestMapping("/users")   //задаёт общий путь для эндпоинтов контроллера.
public class UserController {
    private Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Имейл должен быть указан и содержать символ @");
            throw new ValidationException("Имейл должен быть указан и содержать символ - @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будушем");
        }
        log.info("Пользователь добавлен: {}", user.getLogin());
        user.setId(nextId++);
        users.put(user.getId(), user);

        return user;
    }


    @PutMapping
    public User update(@RequestBody User user) {
        if (users.values().stream().noneMatch(u -> u.getId() == user.getId())) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }

        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Имейл должен быть указан и содержать символ @");
            throw new ValidationException("Имейл должен быть указан и содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        User existingUser = users.get(user.getId());

        existingUser.setEmail(user.getEmail());
        existingUser.setLogin(user.getLogin());
        existingUser.setName(user.getName());
        existingUser.setBirthday(user.getBirthday());

        log.info("Пользователь обновлен: {}", existingUser.getLogin());
        return existingUser;
    }
}
