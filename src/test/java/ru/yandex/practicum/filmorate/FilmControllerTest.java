package ru.yandex.practicum.filmorate;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import static java.util.Objects.requireNonNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DbTest
@SpringBootTest
@AutoConfigureMockMvc
public class FilmControllerTest {
    private static final Film FILM_1 = new Film(0, "Авиатор", new RatingMpa(3, ""), "История о военном летчике",
            LocalDate.parse("2002-12-20"), 160, List.of(new Genre(2, ""), new Genre(1, "")));
    private static final Film FILM_2 = new Film(0, "Тренер Картер", new RatingMpa(5, ""), "История о тренере по " +
            "баскетболу",
            LocalDate.parse("2005-12-20"), 130, List.of(new Genre(2, ""), new Genre(6, "")));
    private static final User USER_1 = new User(0, "lizka@mail.ru", "lizka", "Liza",
            LocalDate.parse("1998-11-06"));
    private static final User USER_2 = new User(0, "rodivonum@mail.ru", "rodivonum", "Rodion",
            LocalDate.parse("1997-04-21"));

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FilmService filmService;
    @Autowired
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;

    private static String getJson(String name, Object... args) throws IOException {
        try (InputStream resourceAsStream = requireNonNull(FilmControllerTest.class.getResourceAsStream(name))) {
            return String.format(new String(resourceAsStream.readAllBytes()), args);
        }
    }

    @Test
    void whenGetFilms_thenStatus200andFilmsReturned() throws Exception {
        Film film1 = filmService.addFilm(FILM_1);
        Film film2 = filmService.addFilm(FILM_2);

        this.mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/film/films.json", film1.getId(), film2.getId()), true));
    }

    @Test
    void whenAddFilm_thenStatus200andFilmAdded() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post("/films")
                        .content(getJson("/film/add_film.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Film film = filmService.findAllFilms().get(0);
        resultActions.andExpect(content().json(getJson("/film/added_film.json", film.getId()), true));
    }

    @Test
    void whenUpdateFilm_thenStatus200andFilmUpdated() throws Exception {
        Film film = filmService.addFilm(FILM_1);

        this.mockMvc.perform(put("/films")
                        .content(getJson("/film/update_film.json", film.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/film/updated_film.json", film.getId()), true));
    }

    @Test
    void whenUpdateFilmWithDuplicateGenres_thenStatus200AndNoDuplicates() throws Exception {
        Film film = filmService.addFilm(FILM_1);

        this.mockMvc.perform(put("/films")
                        .content(getJson("/film/update_film_with_duplicate_genres.json", film.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/film/updated_film_without_duplicate_genres.json", film.getId()),
                        true));
    }

    @Test
    void whenUpdateUnknownFilm_thenStatus404() throws Exception {
        this.mockMvc.perform(put("/films")
                        .content(getJson("/film/film_unknown.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(getJson("/message_for_film/message_for_film_unknown.json"), true));
    }

    @Test
    void whenAddFilmWithIncorrectName_thenStatus400() throws Exception {
        this.mockMvc.perform(post("/films")
                        .content(getJson("/film/film_without_name.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(getJson("/message_for_film/message_for_film_name.json"), true));
    }

    @Test
    void whenAddFilmWithIncorrectDescription_thenStatus400() throws Exception {
        this.mockMvc.perform(post("/films")
                        .content(getJson("/film/film_with_long_description.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(getJson("/message_for_film/message_for_film_description.json"), true));
    }

    @Test
    void whenAddFilmWithIncorrectDate_thenStatus400() throws Exception {
        this.mockMvc.perform(post("/films")
                        .content(getJson("/film/film_with_incorrect_release_date.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(getJson("/message_for_film/message_for_film_release_date.json"), true));
    }

    @Test
    void whenAddFilmWithNegativeDuration_thenStatus400() throws Exception {
        this.mockMvc.perform(post("/films")
                        .content(getJson("/film/film_with_negative_duration.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(getJson("/message_for_film/message_for_film_duration.json"), true));
    }

    @Test
    void whenEmptyRequest_thenStatus400() throws Exception {
        this.mockMvc.perform(post("/films"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenAddLike_thenStatus200AndLikeAdded() throws Exception {
        Film film = filmService.addFilm(FILM_1);
        User user = userService.addUser(USER_1);

        this.mockMvc.perform(put("/films/" + film.getId() + "/like/" + user.getId()))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/films/" + film.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/film/film_with_like.json", film.getId(), user.getId()), true));
    }

    @Test
    void whenAddLikeToUnknownFilm_thenStatus404() throws Exception {
        this.mockMvc.perform(put("/films/9999/like/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(getJson("/message_for_film/message_for_film_unknown.json"), true));
    }

    @Test
    void whenGetPopularFilms_thenStatus200AndFilmsReturned() throws Exception {
        Film film1 = filmService.addFilm(FILM_1);
        Film film2 = filmService.addFilm(FILM_2);
        User user1 = userService.addUser(USER_1);
        User user2 = userService.addUser(USER_2);

        filmService.addLike(film1.getId(), user1.getId());
        filmService.addLike(film1.getId(), user2.getId());
        filmService.addLike(film2.getId(), user2.getId());

        this.mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/film/popular_films.json", film1.getId(), user1.getId(),
                        user2.getId(), film2.getId(), user2.getId()), true));
    }

    @Test
    void whenGetPopularFilmsAndFilmsWithoutLikes_thenStatus200AndFilmsReturned() throws Exception {
        Film film1 = filmService.addFilm(FILM_1);
        Film film2 = filmService.addFilm(FILM_2);

        this.mockMvc.perform(get("/films/popular"))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/film/popular_films_without_likes.json", film1.getId(),
                        film2.getId()), true));
    }

    @Test
    void whenDeleteLike_thenStatus200AndLikeDeleted() throws Exception {
        Film film = filmService.addFilm(FILM_1);
        User user = userService.addUser(USER_1);
        filmService.addLike(film.getId(), user.getId());

        this.mockMvc.perform(delete("/films/" + film.getId() + "/like/" + user.getId()))
                .andExpect(status().isOk());

        this.mockMvc.perform(get("/films/" + film.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/film/film_without_like.json", film.getId()), true));
    }

    @Test
    void whenDeleteUnknownLike_thenStatus404() throws Exception {
        this.mockMvc.perform(delete("/films/9999/like/9999"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(getJson("/message_for_film/message_for_unknown_like.json"), true));
    }

    @Test
    void whenGetAllGenres_thenStatus200AndGenresReturned() throws Exception {
        this.mockMvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/genre/genres.json"), true));
    }

    @Test
    void whenGetGenreById_thenStatus200AndGenreReturned() throws Exception {
        this.mockMvc.perform(get("/genres/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/genre/genre_1.json"), true));
    }

    @Test
    void whenGetGenreByUnknownId_thenStatus404() throws Exception {
        this.mockMvc.perform(get("/genres/9999"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(getJson("/genre/message_for_unknown_genre.json"), true));
    }

    @Test
    void whenGetAllRatings_thenStatus200AndRatingsReturned() throws Exception {
        this.mockMvc.perform(get("/mpa"))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/rating/ratings.json"), true));
    }

    @Test
    void whenGetRatingById_thenStatus200AndRatingReturned() throws Exception {
        this.mockMvc.perform(get("/mpa/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/rating/rating_1.json"), true));
    }

    @Test
    void whenGetRatingByUnknownId_thenStatus404() throws Exception {
        this.mockMvc.perform(get("/mpa/9999"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(getJson("/rating/message_for_unknown_rating.json"), true));
    }

}
