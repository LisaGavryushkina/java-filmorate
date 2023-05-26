package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.lang.Nullable;

@Data
public class Genre {
    @NonNull
    private final int id;
    @Nullable
    private final String name;
}
