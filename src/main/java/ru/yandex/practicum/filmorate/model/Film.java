package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.LinkedHashSet;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Getter
@Setter
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Set<Integer> likes = new HashSet<>();
    private Mpa mpa;
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<Genre> genres = new LinkedHashSet<>();
}
