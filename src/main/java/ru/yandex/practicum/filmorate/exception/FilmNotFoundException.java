package ru.yandex.practicum.filmorate.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FilmNotFoundException extends RuntimeException {

    private final int id;

    @Override
    public String getMessage() {
        return String.format("Фильм [%d] не найден", id);
    }
}
