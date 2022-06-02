package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {

    @Autowired
    private UserController userController;

    @Test
    public void shouldBeIncorrectParameterExceptionWithLackUserIdParameterByGetFilmById() {
        assertThrows(IncorrectParameterException.class, () -> {
            userController.getUserById(null);
        });
    }

    @Test
    public void shouldBeValidationExceptionWithIncorrectEmail() {
        User user = User.builder()
                .name("Ладимир")
                .email("q61bldiyour-mai.xyz")
                .birthday(LocalDate.parse("1973-06-12"))
                .login("LadimirPodnebesnyy51822")
                .build();

        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    public void shouldBeValidationExceptionWithLoginIsBlank() {
        User user = User.builder()
                .name("Ладимир")
                .email("q61bldi@your-mai.xyz")
                .birthday(LocalDate.parse("1973-06-12"))
                .login("")
                .build();

        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    public void shouldBeValidationExceptionWithLoginContainsSpaces() {
        User user = User.builder()
                .name("Ладимир")
                .email("q61bldi@your-mai.xyz")
                .birthday(LocalDate.parse("1973-06-12"))
                .login("LadimirPod nebesnyy518")
                .build();

        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    public void shouldBeNameEqualLoginWithNameIsBlank() throws UserAlreadyExistException, ValidationException {
        User user = User.builder()
                .email("q61bldi1234@your-mai.xyz")
                .birthday(LocalDate.parse("1973-06-12"))
                .login("LadimirPodnebesnyy518")
                .birthday(LocalDate.parse("1992-06-12"))
                .build();

        userController.addUser(user);

        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    public void shouldBeValidationExceptionWithBirthdayInTheFuture() {
        User user = User.builder()
                .email("q61@your-mai.xyz")
                .birthday(LocalDate.parse("2273-06-12"))
                .login("LadimirPodnebesnyy8")
                .build();

        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    public void shouldBeUserNotFoundExceptionWithUserNotAdded() {
        User user = User.builder()
                .id(Integer.MAX_VALUE)
                .email("q61@your-mai.xyz")
                .birthday(LocalDate.parse("1992-06-12"))
                .login("44")
                .build();

        assertThrows(UserNotFoundException.class, () -> userController.updateUser(user));
    }

    @Test
    public void shouldBeUserAlreadyExistExceptionWithUserIsAddedSecondTime() {
        User user = User.builder()
                .email("q6lhkfe1@your-mai.xyz")
                .birthday(LocalDate.parse("1992-06-12"))
                .login("LadimirPodnebesnyy51821")
                .build();

        assertThrows(UserAlreadyExistException.class, () -> {
            userController.addUser(user);
            userController.addUser(user);
        });
    }

    @Test
    public void shouldBeAddingFriend() throws UserAlreadyExistException, ValidationException, UserNotFoundException {
        User user = User.builder()
                .email("vysheslavKukolevskiy589@mail.ru")
                .birthday(LocalDate.parse("1992-06-12"))
                .login("EvgeinyaSaharova604")
                .build();

        User friend = User.builder()
                .email("AgrippinaGronskaya40@mail.ru")
                .birthday(LocalDate.parse("1992-06-12"))
                .login("AgrippinaGronskaya40")
                .build();

        userController.addUser(user);
        userController.addUser(friend);
        userController.addToFriends(user.getId(), friend.getId());

        assertTrue(userController.getUsersFriends(user.getId()).contains(friend));
    }

    @Test
    public void shouldBeIncorrectParameterExceptionWithLackUserIdParameterByAddingFriend() {
        assertThrows(IncorrectParameterException.class, () -> {
            userController.addToFriends(null, 1);
        });
    }

    @Test
    public void shouldBeIncorrectParameterExceptionWithLackFriendIdParameterByAddingFriend() {
        assertThrows(IncorrectParameterException.class, () -> {
            userController.addToFriends(1, null);
        });
    }

    @Test
    public void shouldBeUserNotFoundExceptionWithUserNotAddedBefore() {
        User friend = User.builder()
                .email("BernarKudryavtsev838@mail.ru")
                .birthday(LocalDate.parse("1992-06-12"))
                .login("BernarKudryavtsev838")
                .build();

        assertThrows(UserNotFoundException.class, () -> {
            int friendId = userController.addUser(friend).getId();
            userController.addToFriends(Integer.MAX_VALUE, friendId);
        });
    }

    @Test
    public void shouldBeUserNotFoundExceptionWithFriendNotAddedBefore() {
        User user = User.builder()
                .email("RimmaKiseleva799@mail.ru")
                .birthday(LocalDate.parse("1992-06-12"))
                .login("RimmaKiseleva799")
                .build();

        assertThrows(UserNotFoundException.class, () -> {
            int userId = userController.addUser(user).getId();
            userController.addToFriends(userId, Integer.MAX_VALUE);
        });
    }

    @Test
    public void shouldBeDeleteFriend() throws UserAlreadyExistException, ValidationException, UserNotFoundException {
        User user = User.builder()
                .email("NinelVishnevskaya404@mail.ru")
                .birthday(LocalDate.parse("1992-06-12"))
                .login("NinelVishnevskaya404")
                .build();

        User friend = User.builder()
                .email("LyubomiraUlanova945@mail.ru")
                .birthday(LocalDate.parse("1992-06-12"))
                .login("LyubomiraUlanova945")
                .build();

        userController.addUser(user);
        userController.addUser(friend);
        userController.addToFriends(user.getId(), friend.getId());
        userController.deleteToFriends(user.getId(), friend.getId());

        assertFalse(userController.getUserById(user.getId()).getFriendsId().contains(friend.getId()));
    }

    @Test
    public void shouldBeIncorrectParameterExceptionWithLackUserIdParameterByDeleteFriend() {
        assertThrows(IncorrectParameterException.class, () -> {
            userController.addToFriends(null, 1);
        });
    }

    @Test
    public void shouldBeIncorrectParameterExceptionWithLackFriendIdParameterByDeleteFriend() {
        assertThrows(IncorrectParameterException.class, () -> {
            userController.addToFriends(1, null);
        });
    }

    @Test
    public void shouldBeGetAllUsersFriends()
            throws UserAlreadyExistException, ValidationException, UserNotFoundException {
        User user = User.builder()
                .email("ZaharZhuravel871@mail.ru")
                .birthday(LocalDate.parse("1992-06-12"))
                .login("ZaharZhuravel871")
                .build();

        User friend1 = User.builder()
                .email("AfanasiySekunov317@mail.ru")
                .birthday(LocalDate.parse("1992-06-12"))
                .login("AfanasiySekunov317")
                .build();

        User friend2 = User.builder()
                .email("FotiyPotapov83@mail.ru")
                .birthday(LocalDate.parse("1992-06-12"))
                .login("FotiyPotapov83")
                .build();

        userController.addUser(user);
        userController.addUser(friend1);
        userController.addUser(friend2);

        userController.addToFriends(user.getId(), friend1.getId());
        userController.addToFriends(user.getId(), friend2.getId());

        assertEquals(userController.getUsersFriends(user.getId()), List.of(friend1, friend2));

    }

    @Test
    public void shouldBeIncorrectParameterExceptionWithLackUserIdParameterByGetUsersFriends() {
        assertThrows(IncorrectParameterException.class, () -> {
            userController.getUsersFriends(null);
        });
    }

    @Test
    public void shouldBeGetCommonFriends()
            throws UserAlreadyExistException, ValidationException, UserNotFoundException {
        User user1 = User.builder()
                .email("PankratiyNektov295@mail.ru")
                .birthday(LocalDate.parse("1992-06-12"))
                .login("PankratiyNektov295")
                .build();

        User user2 = User.builder()
                .email("OksanaLeonova655@mail.ru")
                .birthday(LocalDate.parse("1992-06-12"))
                .login("OksanaLeonova655")
                .build();

        User user3 = User.builder()
                .email("NinelYandutova679@mail.ru")
                .birthday(LocalDate.parse("1992-06-12"))
                .login("NinelYandutova679")
                .build();

        User user4 = User.builder()
                .email("VitaliyMiller756@mail.ru")
                .birthday(LocalDate.parse("1992-06-12"))
                .login("VitaliyMiller756")
                .build();

        int userId1 = userController.addUser(user1).getId();
        int userId2 = userController.addUser(user2).getId();
        int userId3 = userController.addUser(user3).getId();
        int userId4 = userController.addUser(user4).getId();
        userController.addToFriends(userId1, userId2);
        userController.addToFriends(userId2, userId3);
        userController.addToFriends(userId1, userId3);
        userController.addToFriends(userId2, userId4);

        Set<User> test = userController.getCommonFriends(userId1, userId2);

        assertTrue(userController.getCommonFriends(userId1, userId2).contains(user3));
    }

    @Test
    public void shouldBeIncorrectParameterExceptionWithLackUserIdParameterByGetCommonFriends() {
        assertThrows(IncorrectParameterException.class, () -> {
            userController.getCommonFriends(null, 1);
        });
    }

    @Test
    public void shouldBeIncorrectParameterExceptionWithLackFriendIdParameterByGetCommonFriends() {
        assertThrows(IncorrectParameterException.class, () -> {
            userController.getCommonFriends(1, null);
        });
    }
}