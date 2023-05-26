package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;
import org.springframework.lang.Nullable;

@Data
public class RatingMpa {
    @NonNull
    private final int id;
    @Nullable
    private final String name;
}
