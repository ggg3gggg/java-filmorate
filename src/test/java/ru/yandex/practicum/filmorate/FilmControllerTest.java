package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    @Test
    void test() {
        FilmController controller = new FilmController();
        Film film = new Film();
        film.setName("");

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void checkThatTheMovieTitleCannotBeEmpty() {
        FilmController controller = new FilmController();
        Film film = new Film();
        film.setName(" ");

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void checkTheMaximumLengthOfTheFilmDescription(){
        FilmController controller = new FilmController();
        Film film = new Film();
        film.setName("Дюна");
        film.setDescription("a".repeat(10));
        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void ckeckLateReleaseDate() {
        FilmController controller = new FilmController();
        Film film = new Film();
        film.setName("Дюна");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);
        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void ckeckZeroFilmDuration() {
        FilmController controller = new FilmController();
        Film film = new Film();
        film.setName("Дюна");
        film.setReleaseDate(LocalDate.of(1900, 12, 27));
        film.setDuration(0);
        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void checkNegativeFilmDuration() {
        FilmController controller = new FilmController();
        Film film = new Film();
        film.setName("Дюна");
        film.setReleaseDate(LocalDate.of(2021, 9, 3));
        film.setDuration(-10);

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void checkTooEarlyReleaseDate() {
        FilmController controller = new FilmController();
        Film film = new Film();
        film.setName("Дюна");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

    @Test
    void checkDescriptionOver200Characters() {
        FilmController controller = new FilmController();
        Film film = new Film();
        film.setName("Дюна");
        film.setDescription("a".repeat(201));
        film.setReleaseDate(LocalDate.of(2021, 9, 3));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> controller.create(film));
    }

}
