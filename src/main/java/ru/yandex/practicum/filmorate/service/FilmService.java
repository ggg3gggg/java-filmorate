package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FilmService {
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public void addLike(int filmId, int userId) {
        userStorage.findById(userId);
        Film film = filmStorage.findById(filmId);
        film.getLikes().add(userId);
        filmStorage.update(film);

        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void removeLike(int filmId, int userId) {
        userStorage.findById(userId);
        Film film = filmStorage.findById(filmId);
        film.getLikes().remove(userId);
        filmStorage.update(film);

        log.info("Пользователь {} удалил лайк фильму {}", userId, filmId);
    }

    public List<Film> getPopularFilms(int count) {

        List<Film> films = filmStorage.findAll().stream()
                .sorted((film1, film2) ->
                        Integer.compare(film2.getLikes().size(), film1.getLikes().size()))
                .limit(count)
                .toList();

        log.info("Получен список популярных фильмов. Количество: {}", films.size());
        return films;
    }
}
