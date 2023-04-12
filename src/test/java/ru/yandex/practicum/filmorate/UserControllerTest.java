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

import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import static java.util.Objects.requireNonNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc

public class UserControllerTest {
    private static final User USER1 = new User(1, "lizka@mail.ru", "lizka", "Liza", LocalDate.parse("1998-11-06"));
    private static final User USER2 = new User(2, "rodivonum@mail.ru", "rodivonum", "Rodion", LocalDate.parse("1997" +
            "-04-21"));

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserController controller;
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
    void whenGetUsers_thenStatus200andUsersReturned() throws Exception {

        controller.addUser(USER1);
        controller.addUser(USER2);

        this.mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/user/users.json"), true));
    }

    @Test
    void whenAddUser_thenStatus200andUserAdded() throws Exception {

        this.mockMvc.perform(post("/users")
                        .content(getJson("/user/user1.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(USER1), true));
    }

    @Test
    void whenUpdateUser_thenStatus200andUserUpdated() throws Exception {
        controller.addUser(USER1);

        this.mockMvc.perform(put("/users")
                        .content(getJson("/user/user_updated.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/user/user_updated.json")));

        this.mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/user/users_after_updating.json"), true));
    }

    @Test
    void whenUpdateUnknownUser_thenStatus404() throws Exception {

        this.mockMvc.perform(put("/users")
                        .content(getJson("/user/user_unknown.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(getJson("/message_for_user/message_user_unknown.json"), true));
    }

    @Test
    void whenAddUserWithIncorrectEmail_thenStatus400() throws Exception {

        this.mockMvc.perform(post("/users")
                        .content(getJson("/user/user_with_incorrect_email.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(getJson("/message_for_user/message_for_user_email.json"), true));
    }

    @Test
    void whenAddUserWithIncorrectLogin_thenStatus400() throws Exception {

        this.mockMvc.perform(post("/users")
                        .content(getJson("/user/user_with_incorrect_login.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(getJson("/message_for_user/message_for_user_login.json"), true));
    }

    @Test
    void whenAddUserWithEmptyName_thenStatus200AndNameAsLogin() throws Exception {

        this.mockMvc.perform(post("/users")
                        .content(getJson("/user/user_with_empty_name.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/user/user_with_name_as_login.json"), true));
    }

    @Test
    void whenAddUserWithIncorrectBirthday_thenStatus400() throws Exception {

        this.mockMvc.perform(post("/users")
                        .content(getJson("/user/user_with_incorrect_birthday.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(getJson("/message_for_user/message_for_user_birthday.json"), true));
    }
}
