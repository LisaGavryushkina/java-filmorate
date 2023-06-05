package ru.yandex.practicum.filmorate;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import ru.yandex.practicum.filmorate.model.User;
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
public class UserControllerTest {
    private static final User USER_1 = new User(0, "lizka@mail.ru", "lizka", "Liza",
            LocalDate.parse("1998-11-06"));
    private static final User USER_2 = new User(0, "rodivonum@mail.ru", "rodivonum", "Rodion",
            LocalDate.parse("1997-04-21"));
    private static final User USER_3 = new User(0, "malena@mail.ru", "malenka", "Alyona",
            LocalDate.parse("1999-10-10"));

    @Autowired
    private ObjectMapper objectMapper;
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
    void whenGetUsers_thenStatus200andUsersReturned() throws Exception {
        User user1 = userService.addUser(USER_1);
        User user2 = userService.addUser(USER_2);

        this.mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/user/users.json", user1.getId(), user2.getId()), true));
    }

    @Test
    void whenAddUser_thenStatus200andUserAdded() throws Exception {
        ResultActions resultActions = this.mockMvc.perform(post("/users")
                .content(getJson("/user/user1.json"))
                .contentType(MediaType.APPLICATION_JSON));
        User user = userService.findAllUsers().get(0);
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/user/user_added.json", user.getId()), true));
    }

    @Test
    void whenGetUserById_thenStatus200andUserReturned() throws Exception {
        User user = userService.addUser(USER_1);

        this.mockMvc.perform(get("/users/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/user/user_added.json", user.getId()), true));
    }

    @Test
    void whenGetUnknownUser_thenStatus404() throws Exception {
        this.mockMvc.perform(get("/users/9999"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(getJson("/message_for_user/message_user_unknown.json"), true));
    }

    @Test
    void whenUpdateUser_thenStatus200andUserUpdated() throws Exception {
        User user = userService.addUser(USER_1);

        this.mockMvc.perform(put("/users")
                        .content(getJson("/user/user_updated.json", user.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/user/user_updated.json", user.getId())));
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
        ResultActions resultActions = this.mockMvc.perform(post("/users")
                .content(getJson("/user/user_with_empty_name.json"))
                .contentType(MediaType.APPLICATION_JSON));
        User user = userService.findAllUsers().get(0);
        resultActions
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/user/user_with_name_as_login.json", user.getId()), true));
    }

    @Test
    void whenAddUserWithIncorrectBirthday_thenStatus400() throws Exception {
        this.mockMvc.perform(post("/users")
                        .content(getJson("/user/user_with_incorrect_birthday.json"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(getJson("/message_for_user/message_for_user_birthday.json"), true));
    }

    @Test
    void whenAddFriend_thenStatus200andUserSubscribed() throws Exception {
        User user1 = userService.addUser(USER_1);
        User user2 = userService.addUser(USER_2);

        this.mockMvc.perform(put("/users/" + user1.getId() + "/friends/" + user2.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/user/user1_with_friend.json", user1.getId(), user2.getId())));

        this.mockMvc.perform(get("/users/" + user2.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/user/user2_without_friend.json", user2.getId()), true));
    }

    @Test
    void whenAddUnknownFriend_thenStatus404() throws Exception {
        User user = userService.addUser(USER_1);

        this.mockMvc.perform(put("/users/" + user.getId() + "/friends/9999"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(getJson("/message_for_user/message_user_unknown.json")));

    }

    @Test
    void whenGetAllFriends_thenStatus200andFriendsReturned() throws Exception {
        User user1 = userService.addUser(USER_1);
        User user2 = userService.addUser(USER_2);
        userService.addFriend(user1.getId(), user2.getId());

        this.mockMvc.perform(get("/users/" + user1.getId() + "/friends"))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/user/user1_friends.json", user2.getId()), true));
    }

    @Test
    void whenDeleteFriend_thenStatus200andFriendDeleted() throws Exception {
        User user1 = userService.addUser(USER_1);
        User user2 = userService.addUser(USER_2);
        userService.addFriend(user1.getId(), user2.getId());

        this.mockMvc.perform(delete("/users/" + user1.getId() + "/friends/" + user2.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/user/user1_without_friend.json", user1.getId())));
    }

    @Test
    void whenDeleteUnknownFriend_thenStatus404() throws Exception {
        User user = userService.addUser(USER_1);

        this.mockMvc.perform(delete("/users/" + user.getId() + "/friends/9999"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(getJson("/message_for_user/message_user_unknown.json")));
    }

    @Test
    void whenGetCommonFriends_thenStatus200andFriendsReturned() throws Exception {
        User user1 = userService.addUser(USER_1);
        User user2 = userService.addUser(USER_2);
        User user3 = userService.addUser(USER_3);

        userService.addFriend(user1.getId(), user3.getId());
        userService.addFriend(user2.getId(), user3.getId());

        this.mockMvc.perform(get("/users/" + user1.getId() + "/friends/common/" + user2.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(getJson("/user/user1_user2_common_friends.json", user3.getId()), true));
    }

    @Test
    void whenGetCommonFriendsWithUnknownFriend_thenStatus404() throws Exception {
        User user1 = userService.addUser(USER_1);

        this.mockMvc.perform(get("/users/" + user1.getId() + "/friends/common/9999"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(getJson("/message_for_user/message_user_unknown.json"), true));
    }


}


