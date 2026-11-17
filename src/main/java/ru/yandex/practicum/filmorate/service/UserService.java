package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserStorage userStorage;

    public void addFriend(int userId, int friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователь {} добавил друга {}", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь {} удалил друга {}", userId, friendId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        User user = userStorage.findById(userId);
        User other = userStorage.findById(otherId);
        List<User> commonFriends = new ArrayList<>();

        for (Integer friendId : user.getFriends()) {
            if (other.getFriends().contains(friendId)) {
                commonFriends.add(userStorage.findById(friendId));
            }
        }

        log.info("Получен список общих друзей пользователя {}. Количество: {}", userId,
                commonFriends.size());
        return commonFriends;
    }

    public List<User> getFriends(int userId) {
        User user = userStorage.findById(userId);

        List<User> friends = user.getFriends().stream()
                .map(userStorage::findById)
                .toList();

        log.info("Получен список друзей пользователя {}. Количество: {}", userId, friends.size());
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
