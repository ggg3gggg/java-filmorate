package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.util.List;

@Repository
public class MpaDbStorage {
    private final JdbcTemplate jdbcTemplate;

    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String FIND_ALL_QUERY =
            "SELECT * FROM mpa ORDER BY id";

    private static final String FIND_BY_ID =
            "SELECT * FROM mpa WHERE id = ?";

    public List<Mpa> findAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, (rs, rowNum) -> {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("id"));
            mpa.setName(rs.getString("name"));
            return mpa;
        });
    }

    public Mpa findById(int id) {
        try {
            return jdbcTemplate.queryForObject(FIND_BY_ID, (rs, rowNum) -> {
                Mpa mpa = new Mpa();
                mpa.setId(rs.getInt("id"));
                mpa.setName(rs.getString("name"));
                return mpa;
            }, id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Рейтинг MPA с ID " + id + " не найден");
        }
    }
}
