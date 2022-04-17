package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private int id;
    private String mail;
    private String login;
    private String name;
    private LocalDate birthdate;
}
