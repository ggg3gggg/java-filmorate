package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;

@Repository
public class GenreDbStorage {
    private final JdbcTemplate jdbcTemplate;
    Genre genre = new Genre();

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private static final String FIND_ALL_QUERY =
            "SELECT * FROM genres ORDER BY id";

    private static final String FIND_BY_ID =
            "SELECT * FROM genres WHERE id = ?";

    public List<Genre> findAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            return genre;
        });
    }

    public Genre findById(int id) {
        try {
            return jdbcTemplate.queryForObject(FIND_BY_ID, (rs, rowNum) -> {
                Genre genre = new Genre();
                genre.setId(rs.getInt("id"));
                genre.setName(rs.getString("name"));
                return genre;
            }, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Жанр с ID " + id + " не найден");
        }
    }
}
