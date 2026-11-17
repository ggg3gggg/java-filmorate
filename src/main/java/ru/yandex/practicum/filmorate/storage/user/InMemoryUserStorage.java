package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User findById(int id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }

        return user;
    }

    @Override
    public User create(User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        User existingUser = users.get(user.getId());
        existingUser.setEmail(user.getEmail());
        existingUser.setLogin(user.getLogin());
        existingUser.setName(user.getName());
        existingUser.setBirthday(user.getBirthday());

        return existingUser;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        findById(userId).getFriends().add(friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        findById(userId).getFriends().remove(friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        return findById(userId).getFriends().stream()
                .map(this::findById)
                .toList();
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        User user = findById(userId);
        User other = findById(otherId);

        return user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .map(this::findById)
                .toList();
    }
}
