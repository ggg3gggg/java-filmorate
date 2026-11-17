package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.List;

@Service
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int userId, int friendId) {
        userStorage.findById(userId);
        userStorage.findById(friendId);

        userStorage.addFriend(userId, friendId);

        log.info("Пользователь {} добавил друга {}", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        userStorage.findById(userId);
        userStorage.findById(friendId);

        userStorage.removeFriend(userId, friendId);

        log.info("Пользователь {} удалил друга {}", userId, friendId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        userStorage.findById(userId);
        userStorage.findById(otherId);

        List<User> commonFriends =
                userStorage.getCommonFriends(userId, otherId);

        log.info("Найдены общие друзья пользователей {} и {}. Количество: {}",
                userId, otherId, commonFriends.size());

        return commonFriends;
    }

    public List<User> getFriends(int userId) {
        userStorage.findById(userId);

        List<User> friends = userStorage.getFriends(userId);

        log.info("Получен список друзей пользователя {}. Количество: {}",
                userId, friends.size());

        return friends;
    }

    public List<User> findAll() {
        return userStorage.findAll().stream().toList();
    }

    public User findById(int id) {
        return userStorage.findById(id);
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        userStorage.findById(user.getId());
        return userStorage.update(user);
    }


}
