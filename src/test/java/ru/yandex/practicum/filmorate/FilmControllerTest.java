package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.time.LocalDate;

public class FilmControllerTest {
    private FilmController controller;

    @BeforeEach
    void setUp() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        JdbcTemplate jdbcTemplate = new JdbcTemplate();

        FilmService filmService = new FilmService(
                filmStorage,
                userStorage,
                new GenreDbStorage(jdbcTemplate),
                new MpaDbStorage(jdbcTemplate)
        );

        controller = new FilmController(filmService);
    }

    @Test
    void test() {
        Film film = new Film();
        film.setName("");

        Assertions.assertThrows(
                ValidationException.class,
                () -> controller.create(film)
        );
    }

    @Test
    void checkThatTheMovieTitleCannotBeEmpty() {
        Film film = new Film();
        film.setName(" ");

        Assertions.assertThrows(
                ValidationException.class,
                () -> controller.create(film)
        );
    }

    @Test
    void checkTheMaximumLengthOfTheFilmDescription() {
        Film film = new Film();
        film.setName("Дюна");
        film.setDescription("a".repeat(201));
        film.setReleaseDate(LocalDate.of(2021, 9, 3));
        film.setDuration(120);

        Assertions.assertThrows(
                ValidationException.class,
                () -> controller.create(film)
        );
    }

    @Test
    void ckeckLateReleaseDate() {
        Film film = new Film();
        film.setName("Дюна");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);

        Assertions.assertThrows(
                ValidationException.class,
                () -> controller.create(film)
        );
    }

    @Test
    void ckeckZeroFilmDuration() {
        Film film = new Film();
        film.setName("Дюна");
        film.setReleaseDate(LocalDate.of(1900, 12, 27));
        film.setDuration(0);

        Assertions.assertThrows(
                ValidationException.class,
                () -> controller.create(film)
        );
    }

    @Test
    void checkNegativeFilmDuration() {
        Film film = new Film();
        film.setName("Дюна");
        film.setReleaseDate(LocalDate.of(2021, 9, 3));
        film.setDuration(-10);

        Assertions.assertThrows(
                ValidationException.class,
                () -> controller.create(film)
        );
    }

    @Test
    void checkTooEarlyReleaseDate() {
        Film film = new Film();
        film.setName("Дюна");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);

        Assertions.assertThrows(
                ValidationException.class,
                () -> controller.create(film)
        );
    }

    @Test
    void checkDescriptionOver200Characters() {
        Film film = new Film();
        film.setName("Дюна");
        film.setDescription("a".repeat(201));
        film.setReleaseDate(LocalDate.of(2021, 9, 3));
        film.setDuration(120);

        Assertions.assertThrows(
                ValidationException.class,
                () -> controller.create(film)
        );
    }
}
