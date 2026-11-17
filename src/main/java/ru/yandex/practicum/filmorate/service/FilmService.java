package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import java.util.List;

@Service
public class FilmService {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreDbStorage genreDbStorage;
    private final MpaDbStorage mpaDbStorage;

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
        validateReferences(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        filmStorage.findById(film.getId());
        validateReferences(film);
        return filmStorage.update(film);
    }
}
