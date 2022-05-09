package ru.yandex.practicum.filmorate.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;
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
    @EqualsAndHashCode.Exclude
    @JsonSerialize(using = DurationSerializer.class)
    private Duration duration;
    private Set<Integer> likes = new HashSet<>();

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