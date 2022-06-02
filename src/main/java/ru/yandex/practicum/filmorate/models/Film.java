package ru.yandex.practicum.filmorate.models;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    @NotNull
    @EqualsAndHashCode.Exclude
    private int id;
    @NotNull
    private String name;
    @NotBlank
    @EqualsAndHashCode.Exclude
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    private int rate;
    @NotNull
    private FilmMPA mpa;
    @NotNull
    @EqualsAndHashCode.Exclude
    private int duration;
    @EqualsAndHashCode.Exclude
    private Set<Integer> likes;

    @Builder
    public Film(int id,
                String name,
                String description,
                LocalDate releaseDate,
                int rate, FilmMPA mpa,
                int duration,
                Set<Integer> likes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.rate = rate;
        this.mpa = mpa;
        this.duration = duration;
        this.likes = likes;

        if (this.likes == null) {
            this.likes = new HashSet<>();
        }
    }

    public void addLike(int userId) {
        likes.add(userId);
    }

    public void deleteLike(int userId) {
        likes.remove(userId);
    }

    public int getLikeCount() {
        return likes.size();
    }
}