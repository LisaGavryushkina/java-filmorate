package ru.yandex.practicum.filmorate.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RatingMpaNotFoundException extends RuntimeException {

    private final int id;

    @Override
    public String getMessage() {
        return String.format("Рейтинг MPA [%d] не найден", id);
    }
}
