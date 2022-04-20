package ru.yandex.practicum.filmorate.validators;

import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.models.User;

import java.time.LocalDate;

public class UserValidator {
    public static void validate(User user) throws ValidationException {
        if (user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("email: не может быть пустой и должна содержать символ @");
        }

        if (user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("login: не может быть пустым и содержать пробелы");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("birthday: не может быть в будущем");
        }
    }
}
