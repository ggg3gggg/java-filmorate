package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film findById(int id) {
        Film film = films.get(id);

        if (film == null) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }

        return film;
    }

    @Override
    public Film create(Film film) {
        film.setId(nextId++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        Film existingFilm = films.get(film.getId());
        existingFilm.setName(film.getName());
        existingFilm.setDescription(film.getDescription());
        existingFilm.setReleaseDate(film.getReleaseDate());
        existingFilm.setDuration(film.getDuration());

        return existingFilm;
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return findAll().stream()
                .sorted((f1, f2) ->
                        Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .toList();
    }

    @Override
    public void addLike(int filmId, int userId) {
        Film film = findById(filmId);
        film.getLikes().add(userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        Film film = findById(filmId);
        film.getLikes().remove(userId);
    }
}

