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
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private UserController userController;

    @Test
    public void shouldBeValidationExceptionUnderIncorrectEmail() {
        User user = new User();
        user.setName("Ладимир");
        user.setEmail("q61bldiyour-mai.xyz");
        user.setBirthday(LocalDate.parse("1973-06-12"));
        user.setLogin("LadimirPodnebesnyy518");

        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    public void shouldBeValidationExceptionUnderLoginIsBlank() {
        User user = new User();
        user.setName("Ладимир");
        user.setEmail("q61bldi@your-mai.xyz");
        user.setBirthday(LocalDate.parse("1973-06-12"));
        user.setLogin("");

        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    public void shouldBeValidationExceptionUnderLoginContainsSpaces() {
        User user = new User();
        user.setName("Ладимир");
        user.setEmail("q61bldi@your-mai.xyz");
        user.setBirthday(LocalDate.parse("1973-06-12"));
        user.setLogin("LadimirPod nebesnyy518");

        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    public void shouldBeNameEqualLoginUnderNameIsBlank() throws UserAlreadyExistException, ValidationException {
        User user = new User();
        user.setEmail("q61bldi1234@your-mai.xyz");
        user.setBirthday(LocalDate.parse("1973-06-12"));
        user.setLogin("LadimirPodnebesnyy518");
        userController.addUser(user);

        assertEquals(user.getName(), user.getLogin());
    }

    @Test
    public void shouldBeValidationExceptionUnderBirthdayInTheFuture() {
        User user = new User();
        user.setEmail("q61@your-mai.xyz");
        user.setBirthday(LocalDate.parse("2273-06-12"));
        user.setLogin("LadimirPodnebesnyy518");

        assertThrows(ValidationException.class, () -> userController.addUser(user));
    }

    @Test
    public void shouldBeUserNotFoundExceptionUnderUserNotAdded() {
        User user = new User();
        user.setId(Integer.MAX_VALUE);
        user.setEmail("q61@your-mai.xyz");
        user.setBirthday(LocalDate.parse("1992-06-12"));
        user.setLogin("LadimirPodnebesnyy518");

        assertThrows(UserNotFoundException.class, () -> userController.updateUser(user));
    }

    @Test
    public void shouldBeUserAlreadyExistExceptionUnderUserIsAddedSecondTime() {
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
    public void shouldBeAddingFriend() {
        User user = new User();
        user.setEmail("vysheslavKukolevskiy589@mail.ru");
        user.setBirthday(LocalDate.parse("1992-06-12"));
        user.setLogin("EvgeinyaSaharova604");

        User friend = new User();
        friend.setEmail("AgrippinaGronskaya40@mail.ru");
        friend.setBirthday(LocalDate.parse("1992-06-12"));
        friend.setLogin("AgrippinaGronskaya40");

        assertDoesNotThrow(() -> {
            userController.addUser(user);
            userController.addUser(friend);
            userController.addToFriends(String.valueOf(user.getId()), String.valueOf(friend.getId()));
        });
    }

    @Test
    public void shouldBeIncorrectParameterExceptionUnderLackUserIdParameterByAddingFriend() {
        assertThrows(IncorrectParameterException.class, () -> {
           userController.addToFriends(null, "1");
        });
    }

    @Test
    public void shouldBeIncorrectParameterExceptionUnderLackFriendIdParameterByAddingFriend() {
        assertThrows(IncorrectParameterException.class, () -> {
            userController.addToFriends("1", null);
        });
    }

    @Test
    public void shouldBeUserNotFoundExceptionUnderUserNotAddedBefore() {
        User friend = new User();
        friend.setEmail("BernarKudryavtsev838@mail.ru");
        friend.setBirthday(LocalDate.parse("1992-06-12"));
        friend.setLogin("BernarKudryavtsev838");

        assertThrows(UserNotFoundException.class, () -> {
            int friendId = userController.addUser(friend).getId();
            userController.addToFriends(String.valueOf(Integer.MAX_VALUE), String.valueOf(friendId));
        });
    }

    @Test
    public void shouldBeUserNotFoundExceptionUnderFriendNotAddedBefore() {
        User user = new User();
        user.setEmail("RimmaKiseleva799@mail.ru");
        user.setBirthday(LocalDate.parse("1992-06-12"));
        user.setLogin("RimmaKiseleva799");

        assertThrows(UserNotFoundException.class, () -> {
            int userId = userController.addUser(user).getId();
            userController.addToFriends(String.valueOf(userId), String.valueOf(Integer.MAX_VALUE));
        });
    }
}