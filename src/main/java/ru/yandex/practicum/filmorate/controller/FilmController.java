package ru.yandex.practicum.filmorate.controller;

import java.util.List;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.service.FilmService;

@RestController
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping("/films")
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Запрос на добавление фильма: {}", film);
        return filmService.addFilm(film);
    }

    @GetMapping("/films/{id}")
    public Film findFilm(@PathVariable int id) {
        log.info("Запрос на получение фильма [{}]", id);
        return filmService.findFilm(id);
    }

    @PutMapping("/films")
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Запрос на обновление фильма: {}", film);
        return filmService.updateFilm(film);
    }

    @GetMapping("/films")
    public List<Film> findAllFilms() {
        log.info("Запрос на получение списка всех фильмов");
        return filmService.findAllFilms();
    }

    @PutMapping("films/{id}/like/{userId}")
    public Film addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Запрос на добавление лайка фильму [{}] от пользователя [{}]", id, userId);
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("films/{id}/like/{userId}")
    public Film deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Запрос на удаление лайка у фильма [{}] от пользователя [{}]", id, userId);
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("films/popular")
    public List<Film> findPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        log.info("Запрос на получение {} самых популярных фильмов", count);
        return filmService.findPopularFilms(count);
    }

    @GetMapping("/genres")
    public List<Genre> findAllGenres() {
        log.info("Запрос на получение списка всех жанров");
        return filmService.findAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre findGenre(@PathVariable int id) {
        log.info("Запрос на получение жанра [{}]", id);
        return filmService.findGenre(id);
    }

    @GetMapping("/mpa")
    public List<RatingMpa> findAllRatingMpa() {
        log.info("Запрос на получение списка всех рейтингов MPA");
        return filmService.findAllRatingMpa();
    }

    @GetMapping("/mpa/{id}")
    public RatingMpa findRatingMpa(@PathVariable int id) {
        log.info("Запрос на получение рейтинга MPA [{}]", id);
        return filmService.findRatingMpa(id);
    }
}
