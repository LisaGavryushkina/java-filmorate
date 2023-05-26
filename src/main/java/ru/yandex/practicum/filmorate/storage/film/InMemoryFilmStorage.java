package ru.yandex.practicum.filmorate.storage.film;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;

public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
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
    public Film addLike(int filmId, int userId) {
        Film film = getFilm(filmId);
        film.addLike(userId);
        return film;
    }

    @Override
    public Film deleteLike(int filmId, int userId) {
        Film film = getFilm(filmId);
        film.deleteLike(userId);
        return film;
    }

    @Override
    public List<Genre> findAllGenres() {
        return null;
    }

    @Override
    public Genre findGenreById(int genreId) {
        return null;
    }

    @Override
    public List<RatingMpa> findAllRatingMpa() {
        return null;
    }

    @Override
    public RatingMpa findRatingMpaById(int ratingMpaId) {
        return null;
    }
}
