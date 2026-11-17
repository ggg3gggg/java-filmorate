package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;


@Repository
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String FIND_BY_ID =
            "SELECT f.*, m.name AS mpa_name " +
                    "FROM films AS f " +
                    "LEFT JOIN mpa AS m ON f.mpa_id = m.id " +
                    "WHERE f.id = ?";
    private static final String FIND_ALL_QUERY =
            "SELECT f.*, m.name AS mpa_name " +
                    "FROM films f " +
                    "LEFT JOIN mpa m ON f.mpa_id = m.id";
    private static final String INSERT_INTO =
            "INSERT INTO films(name, description, release_date, duration, mpa_id) " +
                    "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY =
            "UPDATE films SET name = ?, description = ?, " +
                    "release_date = ?, duration = ?, mpa_id = ? WHERE id = ?";
    private static final String INSERT_GENRE =
            "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
    private static final String DELETE_GENRES =
            "DELETE FROM film_genres WHERE film_id = ?";
    private static final String FIND_GENRES =
            "SELECT g.id, g.name " +
                    "FROM genres AS g " +
                    "JOIN film_genres AS fg ON g.id = fg.genre_id " +
                    "WHERE fg.film_id = ? ";
    private static final String INSERT_LIKE =
            "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE =
            "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String FIND_POPULAR =
            "SELECT f.*, m.name AS mpa_name " +
                    "FROM films AS f " +
                    "LEFT JOIN mpa AS m ON f.mpa_id = m.id " +
                    "LEFT JOIN likes AS l ON f.id = l.film_id " +
                    "GROUP BY f.id, f.name, f.description, f.release_date, " +
                    "f.duration, f.mpa_id, m.name " +
                    "ORDER BY COUNT(l.user_id) DESC " +
                    "LIMIT ?";

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Film> findAll() {
        List<Film> films = jdbcTemplate.query(
                FIND_ALL_QUERY, new FilmRowMapper()
        );

        for (Film film : films) {
            film.setGenres(getFilmGenres(film.getId()));
        }

        return films;
    }

    @Override
    public Film findById(int id) {
        try {
            Film film = jdbcTemplate.queryForObject(
                    FIND_BY_ID, new FilmRowMapper(), id
            );

            film.setGenres(getFilmGenres(id));

            return film;
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Фильм с ID " + id + " не найден");
        }
    }

    @Override
    public Film create(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    INSERT_INTO,
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            if (film.getMpa() != null) {
                ps.setInt(5, film.getMpa().getId());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().intValue());
        for (Genre genre : film.getGenres()) {
            jdbcTemplate.update(
                    INSERT_GENRE,
                    film.getId(),
                    genre.getId()
            );
        }


        return film;
    }

    @Override
    public Film update(Film film) {
        Integer mpaId = film.getMpa() == null
                ? null
                : film.getMpa().getId();

        int rowsUpdated = jdbcTemplate.update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                mpaId,
                film.getId()
        );

        if (rowsUpdated == 0) {
            throw new NotFoundException(
                    "Фильм с ID " + film.getId() + " не найден"
            );
        }

        jdbcTemplate.update(DELETE_GENRES, film.getId());

        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(
                        INSERT_GENRE,
                        film.getId(),
                        genre.getId()
                );
            }
        }

        return film;
    }

    private Set<Genre> getFilmGenres(int filmId) {
        return new LinkedHashSet<>(
                jdbcTemplate.query(FIND_GENRES, (rs, rowNum) -> {
                    Genre genre = new Genre();
                    genre.setId(rs.getInt("id"));
                    genre.setName(rs.getString("name"));
                    return genre;
                }, filmId)
        );
    }

    public void addLike(int filmId, int userId) {
        jdbcTemplate.update(INSERT_LIKE, filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        jdbcTemplate.update(DELETE_LIKE, filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        jdbcTemplate.update(DELETE_LIKE, filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        List<Film> films = jdbcTemplate.query(
                FIND_POPULAR,
                new FilmRowMapper(),
                count
        );

        for (Film film : films) {
            film.setGenres(getFilmGenres(film.getId()));
        }

        return films;
    }

}
