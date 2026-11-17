package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import({FilmDbStorage.class, UserDbStorage.class})
public class FilmDbStorageTest {
    @Autowired
    private FilmDbStorage filmDbStorage;

    @Autowired
    private UserDbStorage userDbStorage;

    @Test
    public void shouldCreateFilm() {
        Film film = createTestFilm("Фильм");

        Film createdFilm = filmDbStorage.create(film);
        Film savedFilm = filmDbStorage.findById(createdFilm.getId());

        assertTrue(createdFilm.getId() > 0);
        assertEquals(createdFilm.getId(), savedFilm.getId());
        assertEquals("Фильм", savedFilm.getName());
        assertEquals("Описание", savedFilm.getDescription());
        assertEquals(100, savedFilm.getDuration());
        assertEquals(
                LocalDate.of(2000, 1, 1),
                savedFilm.getReleaseDate()
        );
        assertEquals(1, savedFilm.getMpa().getId());
    }

    @Test
    public void shouldUpdateFilm() {
        Film film = filmDbStorage.create(
                createTestFilm("Старое название")
        );

        film.setName("Новое название");
        filmDbStorage.update(film);

        Film savedFilm = filmDbStorage.findById(film.getId());

        assertEquals("Новое название", savedFilm.getName());
    }

    @Test
    public void shouldFindAllFilms() {
        filmDbStorage.create(createTestFilm("Фильм 1"));
        filmDbStorage.create(createTestFilm("Фильм 2"));

        Collection<Film> films = filmDbStorage.findAll();

        assertEquals(2, films.size());
    }

    @Test
    public void shouldThrowExceptionWhenFilmNotFound() {
        assertThrows(
                NotFoundException.class,
                () -> filmDbStorage.findById(999)
        );
    }

    @Test
    public void shouldSaveFilmGenres() {
        Film film = createTestFilm("Фильм с жанрами");

        Genre genre1 = new Genre();
        genre1.setId(1);
        genre1.setName("Комедия");

        Genre genre2 = new Genre();
        genre2.setId(2);
        genre2.setName("Драма");

        film.getGenres().add(genre1);
        film.getGenres().add(genre2);

        Film createdFilm = filmDbStorage.create(film);
        Film savedFilm = filmDbStorage.findById(createdFilm.getId());

        assertEquals(2, savedFilm.getGenres().size());

        Set<Integer> genreIds = savedFilm.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toSet());

        assertEquals(Set.of(1, 2), genreIds);
    }

    @Test
    public void shouldSaveFilmLikes() {
        Film film = filmDbStorage.create(
                createTestFilm("Фильм")
        );
        User user = createTestUser("user1");

        filmDbStorage.addLike(film.getId(), user.getId());

        Collection<Film> popularFilms =
                filmDbStorage.getPopularFilms(10);

        assertEquals(1, popularFilms.size());
        assertEquals(
                film.getId(),
                popularFilms.iterator().next().getId()
        );
    }

    @Test
    public void shouldRemoveFilmLike() {
        Film film = filmDbStorage.create(
                createTestFilm("Фильм")
        );
        User user = createTestUser("user1");

        filmDbStorage.addLike(film.getId(), user.getId());
        filmDbStorage.removeLike(film.getId(), user.getId());

        Integer count = userDbStorageCountLikes(
                film.getId(),
                user.getId()
        );

        assertEquals(0, count);
    }

    @Test
    public void shouldGetPopularFilms() {
        Film film1 = filmDbStorage.create(
                createTestFilm("Фильм 1")
        );
        Film film2 = filmDbStorage.create(
                createTestFilm("Фильм 2")
        );

        User user1 = createTestUser("user1");
        User user2 = createTestUser("user2");

        filmDbStorage.addLike(film1.getId(), user1.getId());

        filmDbStorage.addLike(film2.getId(), user1.getId());
        filmDbStorage.addLike(film2.getId(), user2.getId());

        List<Film> popularFilms = List.copyOf(
                filmDbStorage.getPopularFilms(2)
        );

        assertEquals(2, popularFilms.size());
        assertEquals(
                film2.getId(),
                popularFilms.get(0).getId()
        );
        assertEquals(
                film1.getId(),
                popularFilms.get(1).getId()
        );
    }

    private Film createTestFilm(String name) {
        Film film = new Film();
        film.setName(name);
        film.setDescription("Описание");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2000, 1, 1));

        Mpa mpa = new Mpa();
        mpa.setId(1);
        film.setMpa(mpa);

        return film;
    }

    private User createTestUser(String login) {
        User user = new User();
        user.setEmail(login + "@mail.com");
        user.setLogin(login);
        user.setName(login);
        user.setBirthday(LocalDate.of(2000, 1, 1));

        return userDbStorage.create(user);
    }

    private Integer userDbStorageCountLikes(int filmId, int userId) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM likes WHERE film_id = ? AND user_id = ?",
                Integer.class,
                filmId,
                userId
        );
    }

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;
}

