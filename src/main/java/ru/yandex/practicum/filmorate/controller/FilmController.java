package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController  //говорит Spring, что класс обрабатывает HTTP-запросы и возвращает данные, например JSON.
@RequestMapping("/films")  //задаёт общий путь для эндпоинтов контроллера.
public class FilmController {
    List<Film> films = new ArrayList<>();
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private int nextId = 1;

    @GetMapping
    public List<Film> findAll() {
        return films;
    }



    @PostMapping
    public Film create(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Название не может быть пустым");
            throw new ValidationException("Название не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.warn("Описание не должно превышать 200 символов");
            throw new ValidationException("Описание не должно превышать 200 символов");
        }

        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12,28))) {
            log.warn("Дата релиза должна быть не раньше 28.12.1895");
            throw new ValidationException("Дата релиза должна быть не раньше 28.12.1895");
        }

        if (film.getDuration() <= 0) {
            log.warn("Продолжительность фильма должна быть положительным числом");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }

        log.info("Добавлен фильм: {}", film.getName());

        film.setId(nextId++);
        films.add(film);

        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        for (int i = 0; i < films.size(); i++) {
            if (film.getId() == films.get(i).getId()) {
                log.info("Фильм обновлен: {}", film.getName());
                films.set(i, film);
                return film;
            }
        }

        throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
    }
}
