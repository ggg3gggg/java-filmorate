package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;

public class FilmControllerTest {
    @Test
    void test() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        FilmService filmService = new FilmService(filmStorage, userStorage);
        FilmController controller = new FilmController(filmStorage, filmService);
        Film film = new Film();
        film.setName("");
        Assertions.assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void checkThatTheMovieTitleCannotBeEmpty() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        FilmService filmService = new FilmService(filmStorage, userStorage);
        FilmController controller = new FilmController(filmStorage, filmService);
        Film film = new Film();
        film.setName(" ");
        Assertions.assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void checkTheMaximumLengthOfTheFilmDescription() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        FilmService filmService = new FilmService(filmStorage, userStorage);
        FilmController controller = new FilmController(filmStorage, filmService);
        Film film = new Film();
        film.setName("Дюна");
        film.setDescription("a".repeat(10));
        Assertions.assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void ckeckLateReleaseDate() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        FilmService filmService = new FilmService(filmStorage, userStorage);
        FilmController controller = new FilmController(filmStorage, filmService);
        Film film = new Film();
        film.setName("Дюна");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);
        Assertions.assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void ckeckZeroFilmDuration() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        FilmService filmService = new FilmService(filmStorage, userStorage);
        FilmController controller = new FilmController(filmStorage, filmService);
        Film film = new Film();
        film.setName("Дюна");
        film.setReleaseDate(LocalDate.of(1900, 12, 27));
        film.setDuration(0);
        Assertions.assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void checkNegativeFilmDuration() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        FilmService filmService = new FilmService(filmStorage, userStorage);
        FilmController controller = new FilmController(filmStorage, filmService);
        Film film = new Film();
        film.setName("Дюна");
        film.setReleaseDate(LocalDate.of(2021, 9, 3));
        film.setDuration(-10);

        Assertions.assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void checkTooEarlyReleaseDate() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        FilmService filmService = new FilmService(filmStorage, userStorage);
        FilmController controller = new FilmController(filmStorage, filmService);
        Film film = new Film();
        film.setName("Дюна");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);
        Assertions.assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void checkDescriptionOver200Characters() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        FilmService filmService = new FilmService(filmStorage, userStorage);
        FilmController controller = new FilmController(filmStorage, filmService);
        Film film = new Film();
        film.setName("Дюна");
        film.setDescription("a".repeat(201));
        film.setReleaseDate(LocalDate.of(2021, 9, 3));
        film.setDuration(120);
        Assertions.assertThrows(ValidationException.class, () -> controller.create(film));
    }
}
