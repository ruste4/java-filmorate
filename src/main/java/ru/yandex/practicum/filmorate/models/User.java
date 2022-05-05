package ru.yandex.practicum.filmorate.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.*;

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
    private Set<Integer> friendsId = new HashSet<>();

    public Set<Integer> getAllFriendsId() {
        return Collections.unmodifiableSet(friendsId);
    }

    public void addNewFriend(int id) {
        friendsId.add(id);
    }

    public void deleteFriend(int id) {
        friendsId.remove(id);
    }
}
