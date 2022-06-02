package ru.yandex.practicum.filmorate.models;

import lombok.Builder;
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
    @EqualsAndHashCode.Exclude
    private Set<Integer> friendsId;

    @Builder
    public User(int id, String email, String login, String name, LocalDate birthday, Set<Integer> friendsId) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friendsId = friendsId;

        if (this.friendsId == null) {
            this.friendsId = new HashSet<>();
        }
    }

    public void addNewFriend(int id) {
        friendsId.add(id);
    }

    public void deleteFriend(int id) {
        friendsId.remove(id);
    }
}
