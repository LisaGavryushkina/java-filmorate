package ru.yandex.practicum.filmorate;

import java.io.InputStream;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import static java.util.Objects.requireNonNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc

public class FilmControllerTest {
    private static final Film FILM_1 = new Film(1, "Авиатор", "История о военном летчике",
            LocalDate.parse("2002-12-20"), 160);
    private static final Film FILM_2 = new Film(2, "Тренер Картер", "История о тренере по баскетболу",
            LocalDate.parse("2005-12-20"), 130);
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FilmController controller;
    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    public void clear() {
        controller.clear();
    }

    private static String getJson(String name) {

        try (InputStream resourceAsStream = requireNonNull(FilmControllerTest.class.getResourceAsStream(name))) {
            return new String(resourceAsStream.readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void whenGetFilms_thenStatus200andFilmsReturned() throws Exception {

        controller.addFilm(FILM_1);
        controller.addFilm(FILM_2);

        this.mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/film/films.json"), true));
    }

    @Test
    void whenAddFilm_thenStatus200andFilmAdded() throws Exception {

        this.mockMvc.perform(post("/films")
                        .content(getJson("/film/film1.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(FILM_1), true));
    }

    @Test
    void whenUpdateFilm_thenStatus200andFilmUpdated() throws Exception {
        controller.addFilm(FILM_1);

        this.mockMvc.perform(put("/films")
                        .content(getJson("/film/film_updated.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/film/film_updated.json"), true));

        this.mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/film/films_after_updating.json"), true));
    }

    @Test
    void whenUpdateUnknownFilm_thenStatus404() throws Exception {

        this.mockMvc.perform(put("/films")
                        .content(getJson("/film/film_unknown.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(getJson("/message_for_film/message_film_unknown.json"), true));
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
}
