package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest extends ControllerTest {

    @Test
    public void addUserTest() throws Exception {
        // Подготовка
        String urlString = "http://localhost:8080/user";
        User expectedUser = User
                .builder()
                .email("email@yandex.ru")
                .login("some_login")
                .name("Ivan Ivanov")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        String requestJson = gson.toJson(expectedUser);
        // Исполнение
        MvcResult result = mockMvc.perform(post(urlString).contentType(APPLICATION_JSON_UTF8).content(requestJson)).andReturn();
        String response = result.getResponse().getContentAsString();
        User actualUser = gson.fromJson(response, User.class);
        // Проверка
        expectedUser.setId(1);
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void updateUserTest() throws Exception {
        // Подготовка
        String urlString = "http://localhost:8080/user";
        User user = User
                .builder()
                .email("email@yandex.ru")
                .login("some_login")
                .name("Ivan Ivanov")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        String requestJson = gson.toJson(user);
        mockMvc.perform(post(urlString).contentType(APPLICATION_JSON_UTF8).content(requestJson));
        // Исполнение
        User expectedUser = User
                .builder()
                .id(1)
                .email("email@yandex.ru")
                .login("some_login")
                .name("")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
        requestJson = gson.toJson(expectedUser);
        MvcResult result = mockMvc.perform(put(urlString).contentType(APPLICATION_JSON_UTF8).content(requestJson)).andReturn();
        String response = result.getResponse().getContentAsString();
        User actualFilm = gson.fromJson(response, User.class);
        expectedUser.setName(expectedUser.getLogin());
        // Проверка
        assertEquals(expectedUser, actualFilm);
    }
}