
package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import(UserDbStorage.class)
public class UserDbStorageTest {

    @Autowired
    private UserDbStorage userDbStorage;

    @Test
    public void shouldCreateUser() {
        User user = createTestUser("user1");

        User savedUser = userDbStorage.findById(user.getId());

        assertTrue(user.getId() > 0);
        assertEquals(user.getId(), savedUser.getId());
        assertEquals("user1@mail.com", savedUser.getEmail());
        assertEquals("user1", savedUser.getLogin());
        assertEquals("user1", savedUser.getName());
        assertEquals(LocalDate.of(2000, 1, 1), savedUser.getBirthday());
    }

    @Test
    public void shouldUpdateUser() {
        User user = createTestUser("user1");

        user.setName("Новое имя");
        userDbStorage.update(user);

        User savedUser = userDbStorage.findById(user.getId());

        assertEquals("Новое имя", savedUser.getName());
    }

    @Test
    public void shouldFindAllUsers() {
        createTestUser("user1");
        createTestUser("user2");

        Collection<User> users = userDbStorage.findAll();

        assertEquals(2, users.size());
    }

    @Test
    public void shouldThrowExceptionWhenUserNotFound() {
        assertThrows(
                NotFoundException.class,
                () -> userDbStorage.findById(999)
        );
    }

    @Test
    public void shouldAddFriend() {
        User user1 = createTestUser("user1");
        User user2 = createTestUser("user2");

        userDbStorage.addFriend(user1.getId(), user2.getId());

        Collection<User> friends = userDbStorage.getFriends(user1.getId());

        assertEquals(1, friends.size());
        assertEquals(user2.getId(), friends.iterator().next().getId());

        // Дружба односторонняя
        assertTrue(userDbStorage.getFriends(user2.getId()).isEmpty());
    }

    @Test
    public void shouldRemoveFriend() {
        User user1 = createTestUser("user1");
        User user2 = createTestUser("user2");

        userDbStorage.addFriend(user1.getId(), user2.getId());
        userDbStorage.removeFriend(user1.getId(), user2.getId());

        assertTrue(userDbStorage.getFriends(user1.getId()).isEmpty());
    }

    @Test
    public void shouldGetCommonFriends() {
        User user1 = createTestUser("user1");
        User user2 = createTestUser("user2");
        User user3 = createTestUser("user3");

        userDbStorage.addFriend(user1.getId(), user3.getId());
        userDbStorage.addFriend(user2.getId(), user3.getId());

        Collection<User> commonFriends =
                userDbStorage.getCommonFriends(
                        user1.getId(),
                        user2.getId()
                );

        assertEquals(1, commonFriends.size());
        assertEquals(
                user3.getId(),
                commonFriends.iterator().next().getId()
        );
    }

    private User createTestUser(String login) {
        User user = new User();
        user.setEmail(login + "@mail.com");
        user.setLogin(login);
        user.setName(login);
        user.setBirthday(LocalDate.of(2000, 1, 1));

        return userDbStorage.create(user);
    }
}

