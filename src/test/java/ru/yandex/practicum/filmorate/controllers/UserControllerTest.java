package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.IncorrectParameterException;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
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
        User user = new User();
        user.setName("Ладимир");
        user.setEmail("q61bldiyour-mai.xyz");
        user.setBirthday(LocalDate.parse("1973-06-12"));
        user.setLogin("LadimirPodnebesnyy518");

        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    public void shouldBeValidationExceptionWithLoginIsBlank() {
        User user = new User();
        user.setName("Ладимир");
        user.setEmail("q61bldi@your-mai.xyz");
        user.setBirthday(LocalDate.parse("1973-06-12"));
        user.setLogin("");

        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    public void shouldBeValidationExceptionWithLoginContainsSpaces() {
        User user = new User();
        user.setName("Ладимир");
        user.setEmail("q61bldi@your-mai.xyz");
        user.setBirthday(LocalDate.parse("1973-06-12"));
        user.setLogin("LadimirPod nebesnyy518");

        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    public void shouldBeNameEqualLoginWithNameIsBlank() throws UserAlreadyExistException, ValidationException {
        User user = new User();
        user.setEmail("q61bldi1234@your-mai.xyz");
        user.setBirthday(LocalDate.parse("1973-06-12"));
        user.setLogin("LadimirPodnebesnyy518");
        userController.addUser(user);

        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    public void shouldBeValidationExceptionWithBirthdayInTheFuture() {
        User user = new User();
        user.setEmail("q61@your-mai.xyz");
        user.setBirthday(LocalDate.parse("2273-06-12"));
        user.setLogin("LadimirPodnebesnyy518");

        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    public void shouldBeUserNotFoundExceptionWithUserNotAdded() {
        User user = new User();
        user.setId(Integer.MAX_VALUE);
        user.setEmail("q61@your-mai.xyz");
        user.setBirthday(LocalDate.parse("1992-06-12"));
        user.setLogin("LadimirPodnebesnyy518");

        assertThrows(UserNotFoundException.class, () -> userController.updateUser(user));
    }

    @Test
    public void shouldBeUserAlreadyExistExceptionWithUserIsAddedSecondTime() {
        User user = new User();
        user.setEmail("q6lhkfe1@your-mai.xyz");
        user.setBirthday(LocalDate.parse("1992-06-12"));
        user.setLogin("LadimirPodnebesnyy518");

        assertThrows(UserAlreadyExistException.class, () -> {
            userController.addUser(user);
            userController.addUser(user);
        });
    }

    @Test
    public void shouldBeAddingFriend() throws UserAlreadyExistException, ValidationException, UserNotFoundException {
        User user = new User();
        user.setEmail("vysheslavKukolevskiy589@mail.ru");
        user.setBirthday(LocalDate.parse("1992-06-12"));
        user.setLogin("EvgeinyaSaharova604");

        User friend = new User();
        friend.setEmail("AgrippinaGronskaya40@mail.ru");
        friend.setBirthday(LocalDate.parse("1992-06-12"));
        friend.setLogin("AgrippinaGronskaya40");

        userController.addUser(user);
        userController.addUser(friend);
        userController.addToFriends(user.getId(), friend.getId());

        assertTrue(userController.getUserById(user.getId()).getAllFriendsId().contains(friend.getId()));
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
        User friend = new User();
        friend.setEmail("BernarKudryavtsev838@mail.ru");
        friend.setBirthday(LocalDate.parse("1992-06-12"));
        friend.setLogin("BernarKudryavtsev838");

        assertThrows(UserNotFoundException.class, () -> {
            int friendId = userController.addUser(friend).getId();
            userController.addToFriends(Integer.MAX_VALUE, friendId);
        });
    }

    @Test
    public void shouldBeUserNotFoundExceptionWithFriendNotAddedBefore() {
        User user = new User();
        user.setEmail("RimmaKiseleva799@mail.ru");
        user.setBirthday(LocalDate.parse("1992-06-12"));
        user.setLogin("RimmaKiseleva799");

        assertThrows(UserNotFoundException.class, () -> {
            int userId = userController.addUser(user).getId();
            userController.addToFriends(userId, Integer.MAX_VALUE);
        });
    }

    @Test
    public void shouldBeDeleteFriend() throws UserAlreadyExistException, ValidationException, UserNotFoundException {
        User user = new User();
        user.setEmail("NinelVishnevskaya404@mail.ru");
        user.setBirthday(LocalDate.parse("1992-06-12"));
        user.setLogin("NinelVishnevskaya404");

        User friend = new User();
        friend.setEmail("LyubomiraUlanova945@mail.ru");
        friend.setBirthday(LocalDate.parse("1992-06-12"));
        friend.setLogin("LyubomiraUlanova945");

        userController.addUser(user);
        userController.addUser(friend);
        userController.addToFriends(user.getId(), friend.getId());
        userController.deleteToFriends(user.getId(), friend.getId());

        assertFalse(userController.getUserById(user.getId()).getAllFriendsId().contains(friend.getId()));
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
        User user = new User();
        user.setEmail("ZaharZhuravel871@mail.ru");
        user.setBirthday(LocalDate.parse("1992-06-12"));
        user.setLogin("ZaharZhuravel871");

        User friend1 = new User();
        friend1.setEmail("AfanasiySekunov317@mail.ru");
        friend1.setBirthday(LocalDate.parse("1992-06-12"));
        friend1.setLogin("AfanasiySekunov317");

        User friend2 = new User();
        friend2.setEmail("FotiyPotapov83@mail.ru");
        friend2.setBirthday(LocalDate.parse("1992-06-12"));
        friend2.setLogin("FotiyPotapov83");

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
        User user1 = new User();
        user1.setEmail("PankratiyNektov295@mail.ru");
        user1.setBirthday(LocalDate.parse("1992-06-12"));
        user1.setLogin("PankratiyNektov295");

        User user2 = new User();
        user2.setEmail("OksanaLeonova655@mail.ru");
        user2.setBirthday(LocalDate.parse("1992-06-12"));
        user2.setLogin("OksanaLeonova655");

        User user3 = new User();
        user3.setEmail("NinelYandutova679@mail.ru");
        user3.setBirthday(LocalDate.parse("1992-06-12"));
        user3.setLogin("NinelYandutova679");

        User user4 = new User();
        user4.setEmail("VitaliyMiller756@mail.ru");
        user4.setBirthday(LocalDate.parse("1992-06-12"));
        user4.setLogin("VitaliyMiller756");

        int userId1 = userController.addUser(user1).getId();
        int userId2 = userController.addUser(user2).getId();
        int userId3 = userController.addUser(user3).getId();
        int userId4 = userController.addUser(user4).getId();
        userController.addToFriends(userId1, userId2);
        userController.addToFriends(userId2, userId3);
        userController.addToFriends(userId1, userId3);
        userController.addToFriends(userId2, userId4);

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