package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController   //говорит Spring, что класс обрабатывает HTTP-запросы и возвращает данные, например JSON.
@RequestMapping("/users")   //задаёт общий путь для эндпоинтов контроллера.
public class UserController {
    List<User> users = new ArrayList<>();
    private int nextId = 1;
    private static final Logger log = LoggerFactory.getLogger(UserController.class);


    @GetMapping
    public List<User> findAll() {
        return users;
    }

    @PostMapping
    public User create(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Имейл должен быть указан и содержать символ @");
            throw new ValidationException("Имейл должен быть указан и содержать символ - @");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будушем");
        }
        log.info("Пользователь добавлен: {}", user.getLogin());

        user.setId(nextId++);
        users.add(user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        for (int i = 0; i < users.size(); i++) {
            if (user.getId() == users.get(i).getId()) {
                log.info("Пользователь обновлен: {}", user.getLogin());
                users.set(i, user);
                return user;
            }
        }

        throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
    }

}
