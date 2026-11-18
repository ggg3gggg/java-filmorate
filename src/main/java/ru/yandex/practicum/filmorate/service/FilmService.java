package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import java.time.LocalDate;
import java.util.List;

@Service
public class FilmService {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;

    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage, GenreDbStorage genreDbStorage, MpaDbStorage mpaDbStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.genreDbStorage = genreDbStorage;
        this.mpaDbStorage = mpaDbStorage;
    }

    public void addLike(int filmId, int userId) {
        userStorage.findById(userId);
        filmStorage.findById(filmId);

        filmStorage.addLike(filmId, userId);

        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void removeLike(int filmId, int userId) {
        userStorage.findById(userId);
        filmStorage.findById(filmId);

        filmStorage.removeLike(filmId, userId);

        log.info("Пользователь {} удалил лайк фильму {}", userId, filmId);
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> films = filmStorage.getPopularFilms(count);

        log.info("Получен список популярных фильмов. Количество: {}", films.size());

        return films;
    }

    public List<Film> findAll() {
        return filmStorage.findAll().stream().toList();
    }

    public Film findById(int id) {
        return filmStorage.findById(id);
    }

    public Film create(Film film) {
        log.info("NAME = {}", film.getName());
        log.info("MPA = {}", film.getMpa() == null ? null : film.getMpa().getId());
        log.info("GENRES COUNT = {}",
                film.getGenres() == null ? null : film.getGenres().size());

        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название не может быть пустым");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание не должно превышать 200 символов");
        }

        if (film.getReleaseDate() == null ||
                film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза должна быть не раньше 28.12.1895");
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }

        validateReferences(film);

        Film createdFilm = filmStorage.create(film);
        log.info("Добавлен фильм: {}", createdFilm.getName());

        return createdFilm;
    }

    public Film update(Film film) {
        filmStorage.findById(film.getId());
        validateReferences(film);
        return filmStorage.update(film);
    }

    private void validateReferences(Film film) {
        if (film.getMpa() != null) {
            mpaDbStorage.findById(film.getMpa().getId());
        }

        if (film.getGenres() != null) {
            for (var genre : film.getGenres()) {
                genreDbStorage.findById(genre.getId());
            }
        }
    }
}
