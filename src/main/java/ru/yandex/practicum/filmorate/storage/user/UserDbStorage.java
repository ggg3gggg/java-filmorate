package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;

@Repository
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_INTO = "INSERT INTO users(email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_QUERY =
            "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
    private static final String INSERT_FRIEND =
            "INSERT INTO friendships (user_id, friend_id) VALUES (?, ?)";
    private static final String FIND_FRIENDS =
            "SELECT u.* FROM users AS u " +
                    "JOIN friendships AS f ON u.id = f.friend_id " +
                    "WHERE f.user_id = ?";
    private static final String FIND_COMMON_FRIENDS =
            "SELECT u.* FROM users AS u " +
                    "JOIN friendships AS f1 ON u.id = f1.friend_id " +
                    "JOIN friendships AS f2 ON u.id = f2.friend_id " +
                    "WHERE f1.user_id = ? AND f2.user_id = ?";
    private static final String DELETE_FRIEND =
            "DELETE FROM friendships WHERE user_id = ? AND friend_id = ?";

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> findAll() {
        return jdbcTemplate.query(FIND_ALL_QUERY, new UserRowMapper());
    }

    @Override
    public User findById(int id) {
        try  {
            return jdbcTemplate.queryForObject(FIND_BY_ID, new UserRowMapper(), id);
        }  catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден");
        }
    }

    @Override
    public User create(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    INSERT_INTO,
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, user.getBirthday() != null
                    ? Date.valueOf(user.getBirthday())
                    : null);
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().intValue());

        return user;
    }

    @Override
    public User update(User user) {
        int rowsUpdated = jdbcTemplate.update(
                UPDATE_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );

        if (rowsUpdated == 0) {
            throw new NotFoundException(
                    "Пользователь с ID " + user.getId() + " не найден"
            );
        }
        return user;
    }

    public void addFriend(int userId, int friendId) {
        jdbcTemplate.update(INSERT_FRIEND, userId, friendId);
    }

    public List<User> getFriends(int userId) {
        return jdbcTemplate.query(
                FIND_FRIENDS,
                new UserRowMapper(),
                userId
        );
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        return jdbcTemplate.query(
                FIND_COMMON_FRIENDS,
                new UserRowMapper(),
                userId,
                otherId
        );
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        jdbcTemplate.update(DELETE_FRIEND, userId, friendId);
    }
}
