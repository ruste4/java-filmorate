package ru.yandex.practicum.filmorate.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @EqualsAndHashCode.Exclude
    private String title;
    @EqualsAndHashCode.Exclude
    private String description;
    @EqualsAndHashCode.Exclude
    private LocalDate releaseDate;
    @EqualsAndHashCode.Exclude
    private Duration duration;
}
