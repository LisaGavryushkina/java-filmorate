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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.info("Запрос на добавление фильма: {}", film);
        return filmService.addFilm(film);
    }

    @GetMapping("/{id}")
    public Film findFilm(@PathVariable int id) {
        log.info("Запрос на получение фильма [{}]", id);
        return filmService.findFilm(id);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Запрос на обновление фильма: {}", film);
        return filmService.updateFilm(film);
    }

    @GetMapping
    public List<Film> findAllFilms() {
        log.info("Запрос на получение списка всех фильмов");
        return filmService.findAllFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Запрос на добавление лайка фильму [{}] от пользователя [{}]", id, userId);
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Запрос на удаление лайка у фильма [{}] от пользователя [{}]", id, userId);
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> findPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        log.info("Запрос на получение {} самых популярных фильмов", count);
        return filmService.findPopularFilms(count);
    }
}
