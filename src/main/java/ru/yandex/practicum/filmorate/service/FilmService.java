package ru.yandex.practicum.filmorate.service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

@Service
@Slf4j
public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addFilm(Film film) {
        Film added = filmStorage.add(film);
        log.info("Добавлен новый фильм: {}", added);
        return added;
    }

    public Film updateFilm(Film film) {
        Film updated = filmStorage.update(film);
        log.info("Информация о фильме обновлена: {}", updated);
        return updated;
    }

    public List<Film> findAllFilms() {
        List<Film> films = filmStorage.findAll();
        log.info("Вернули список всех фильмов: {}", films);
        return films;
    }

    public Film addLike(int filmId, int userId) {
        Film updated = filmStorage.addLike(filmId, userId);
        log.info("Добавлен лайк фильму {} от пользователя [{}]", updated, userId);
        return updated;
    }

    public Film findFilm(int id) {
        Film film = filmStorage.getFilm(id);
        log.info("Вернули фильм [{}] : [{}]", id, film);
        return film;
    }

    public Film deleteLike(int filmId, int userId) {
        Film updated = filmStorage.deleteLike(filmId, userId);
        log.info("Удален лайк у фильма {} от пользователя [{}]", updated, userId);
        return updated;
    }

    public List<Film> findPopularFilms(int count) {
        List<Film> popularFilms = filmStorage.findPopularFilms(count);
        log.info("Вернули {} самых популярных фильмов: {}", count, popularFilms);
        return popularFilms;
    }

    public List<Genre> findAllGenres() {
        List<Genre> genres = filmStorage.findAllGenres();
        log.info("Вернули список всех жанров: {}", genres);
        return genres;
    }

    public Genre findGenre(int genreId) {
        Genre genre = filmStorage.findGenreById(genreId);
        log.info("Вернули жанр [{}] : [{}]", genreId, genre);
        return genre;
    }

    public List<RatingMpa> findAllRatingMpa() {
        List<RatingMpa> ratings = filmStorage.findAllRatingMpa();
        log.info("Вернули список всех рейтингов MPA: {}", ratings);
        return ratings;
    }

    public RatingMpa findRatingMpa(int ratingMpaId) {
        RatingMpa mpa = filmStorage.findRatingMpaById(ratingMpaId);
        log.info("Вернули рейтинг MPA [{}] : [{}]", ratingMpaId, mpa);
        return mpa;
    }
}
