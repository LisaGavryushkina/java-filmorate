package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.NonNull;
import lombok.Setter;

import ru.yandex.practicum.filmorate.controller.LocalDateConstraint;

@Data
public class Film {
    @NonNull
    @Setter
    private int id;
    @NotBlank(message = "Название не может быть пустым")
    private final String name;
    @Size(max = 200, message = "Описание не может быть длиннее 200 символов")
    private final String description;
    @LocalDateConstraint(message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    private final LocalDate releaseDate;
    @Positive(message = "Длительность фильма не может быть отрицательной")
    private final int duration;
}
