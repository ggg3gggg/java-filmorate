package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;


@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }

    @GetMapping
    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable int id) {
        return filmStorage.findById(id);
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название не может быть пустым");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.error("Описание не должно превышать 200 символов");
            throw new ValidationException("Описание не должно превышать 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза должна быть не раньше 28.12.1895");
            throw new ValidationException("Дата релиза должна быть не раньше 28.12.1895");
        }
        if (film.getDuration() <= 0) {
            log.error("Продолжительность фильма должна быть положительным числом");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }

        Film createdFilm = filmStorage.create(film);
        log.info("Добавлен фильм: {}", createdFilm.getName());
        return createdFilm;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        filmStorage.findById(film.getId());

        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Название не может быть пустым");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.error("Описание не должно превышать 200 символов");
            throw new ValidationException("Описание не должно превышать 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза должна быть не раньше 28.12.1895");
            throw new ValidationException("Дата релиза должна быть не раньше 28.12.1895");
        }
        if (film.getDuration() <= 0) {
            log.error("Продолжительность фильма должна быть положительным числом");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }

        Film updatedFilm = filmStorage.update(film);
        log.info("Фильм обновлен: {}", updatedFilm.getName());
        return updatedFilm;
    }
}

