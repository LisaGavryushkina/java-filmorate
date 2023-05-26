package ru.yandex.practicum.filmorate.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LikeNotFoundException extends RuntimeException {
    private final int filmId;
    private final int userId;

    @Override
    public String getMessage() {
        return String.format("Лайк для фильма [%d] от пользователя [%d] не найден", filmId, userId);
    }

}
