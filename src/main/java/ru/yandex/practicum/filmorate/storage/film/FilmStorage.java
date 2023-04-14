package ru.yandex.practicum.filmorate.storage.film;

import java.util.List;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {

    Film add(Film film);

    Film update(Film film);

    List<Film> findAll();

    void clear();

    Film getFilm(int id);


    List<Film> findPopularFilms(int count);

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);
}
