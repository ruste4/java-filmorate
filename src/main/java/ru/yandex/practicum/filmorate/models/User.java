package ru.yandex.practicum.filmorate.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    @EqualsAndHashCode.Exclude
    private int id;
    @Email
    private String email;
    @NotBlank
    @EqualsAndHashCode.Exclude
    private String login;
    @EqualsAndHashCode.Exclude
    private String name;
    @Past
    @EqualsAndHashCode.Exclude
    private LocalDate birthday;
}
