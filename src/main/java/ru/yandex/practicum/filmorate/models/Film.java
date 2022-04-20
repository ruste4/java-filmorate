package ru.yandex.practicum.filmorate.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    @NotNull
    @EqualsAndHashCode.Exclude
    private int id;
    @NotNull
    private String name;
    @NotNull
    @EqualsAndHashCode.Exclude
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    @EqualsAndHashCode.Exclude
    private Duration duration;
}
