package ru.yandex.practicum.filmorate.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
public class User {
    private int id;
    @EqualsAndHashCode.Exclude
    private String mail;
    @EqualsAndHashCode.Exclude
    private String login;
    @EqualsAndHashCode.Exclude
    private String name;
    @EqualsAndHashCode.Exclude
    private LocalDate birthdate;
}
