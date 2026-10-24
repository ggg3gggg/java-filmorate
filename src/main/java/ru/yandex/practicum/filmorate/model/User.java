package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import java.util.Set;
import java.util.HashSet;
import java.time.LocalDate;

@Getter
@Setter
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Integer> friends = new HashSet<>();
}
