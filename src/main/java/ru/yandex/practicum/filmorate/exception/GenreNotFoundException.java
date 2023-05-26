package ru.yandex.practicum.filmorate.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

public class GenreNotFoundException extends RuntimeException {

    private final int id;

    @Override
    public String getMessage() {
        return String.format("Жанр [%d] не найден", id);
    }
}
