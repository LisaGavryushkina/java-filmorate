package ru.yandex.practicum.filmorate.storage.film;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new ConcurrentHashMap<>();
    private int id;

    private int createId() {
        return ++id;
    }

    @Override
    public Film add(Film film) {
        film.setId(createId());
        films.put(film.getId(), film);
        return film;
    }


    @Override
    public Film update(Film film) {
        int filmId = film.getId();
        if (!films.containsKey(filmId)) {
            throw new FilmNotFoundException(filmId);
        }
        films.put(filmId, film);
        return film;
    }

    @Override
    public List<Film> findAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void clear() {
        films.clear();
        id = 0;
    }

    @Override
    public Film getFilm(int id) {
        return Optional.ofNullable(films.get(id)).orElseThrow(() -> new FilmNotFoundException(id));
    }

    @Override
    public List<Film> findPopularFilms(int count) {
        if (count > films.size()) {
            count = films.size();
        }
        return films.values().stream()
                .sorted(Comparator.comparing(Film::getNumberOfLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public void addLike(int filmId, int userId) {
        getFilm(filmId).addLike(userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        getFilm(filmId).deleteLike(userId);
    }
}
