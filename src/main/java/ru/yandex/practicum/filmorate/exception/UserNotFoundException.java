package ru.yandex.practicum.filmorate.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserNotFoundException extends RuntimeException {

    private final int id;

    @Override
    public String getMessage() {
        return String.format("Пользователь [%d] не найден", id);
    }
}
