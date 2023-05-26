package ru.yandex.practicum.filmorate.storage.film;

import java.util.List;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;

public interface FilmStorage {

    Film add(Film film);

    Film update(Film film);

    List<Film> findAll();

    void clear();

    Film getFilm(int id);

    List<Film> findPopularFilms(int count);

    Film addLike(int filmId, int userId);

    Film deleteLike(int filmId, int userId);

    List<Genre> findAllGenres();

    Genre findGenreById(int genreId);

    List<RatingMpa> findAllRatingMpa();

    RatingMpa findRatingMpaById(int ratingMpaId);
}
