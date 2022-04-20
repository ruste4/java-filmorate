package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exceptions.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.exceptions.UserNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;

import java.time.LocalDate;

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
        user.setId(Integer.MAX_VALUE);
        user.setEmail("q6lhkfe1@your-mai.xyz");
        user.setBirthday(LocalDate.parse("1992-06-12"));
        user.setLogin("LadimirPodnebesnyy518");

        assertThrows(UserAlreadyExistException.class, () -> {
            userController.addUser(user);
            userController.addUser(user);
        });
    }
}