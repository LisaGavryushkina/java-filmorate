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

    public void clear() {
        filmService.clear();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        Film added = filmService.addFilm(film);
        log.info("Добавлен новый фильм: {}", added);
        return added;
    }

    @GetMapping("/{id}")
    public Film findFilm(@PathVariable int id) {
        return filmService.findFilm(id);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        Film updated = filmService.updateFilm(film);
        log.info("Информация о фильме обновлена: {}", updated);
        return updated;
    }

    @GetMapping
    public List<Film> findAllFilms() {
        return filmService.findAllFilms();
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable int id, @PathVariable int userId) {
        Film updated = filmService.addLike(id, userId);
        log.info("Добавлен лайк фильму с id: {} от пользователя с id: {}", id, userId);
        return updated;
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable int id, @PathVariable int userId) {
        Film updated = filmService.deleteLike(id, userId);
        log.info("Удален лайк у фильма с id: {} от пользователя с id: {}", id, userId);
        return updated;
    }

    @GetMapping("/popular")
    public List<Film> findPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.findPopularFilms(count);
    }
}
