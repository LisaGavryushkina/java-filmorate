package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;

import ru.yandex.practicum.filmorate.exception.UserNotFoundException;

@Data
public class Film {
    @NonNull
    @Setter
    private int id;
    @NotBlank(message = "Название не может быть пустым")
    private final String name;
    @NonNull
    private final RatingMpa mpa;
    @Size(max = 200, message = "Описание не может быть длиннее 200 символов")
    private final String description;
    @LocalDateConstraint(message = "Дата релиза не может быть раньше 28 декабря 1895 года")
    private final LocalDate releaseDate;
    @Positive(message = "Длительность фильма не может быть отрицательной")
    private final int duration;
    private final List<Genre> genres;
    @Setter
    private Set<Integer> likes = new HashSet<>();

    public void addLike(int userId) {
        likes.add(userId);
    }

    public void deleteLike(int userId) {
        if (!likes.contains(userId)) {
            throw new UserNotFoundException(userId);
        }
        likes.remove(userId);
    }

    @JsonIgnore
    public int getNumberOfLikes() {
        return likes.size();
    }
}
