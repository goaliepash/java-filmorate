package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@ExtendWith(SpringExtension.class)
@WebMvcTest(FilmController.class)
class FilmControllerTest extends ControllerTest {

    @Test
    public void addFilmTest() throws Exception {
        // Подготовка
        String urlString = "/film";
        Film expectedFilm = Film
                .builder()
                .name("Pulp Fiction")
                .description("Black comedy criminal film")
                .releaseDate(LocalDate.of(1994, 10, 14))
                .duration(195)
                .build();
        String requestJson = gson.toJson(expectedFilm);
        // Исполнение
        MvcResult result = mockMvc.perform(post(urlString).contentType(APPLICATION_JSON_UTF8).content(requestJson)).andReturn();
        String response = result.getResponse().getContentAsString();
        Film actualFilm = gson.fromJson(response, Film.class);
        // Проверка
        expectedFilm.setId(1);
        assertEquals(expectedFilm, actualFilm);
    }

    @Test
    public void updateFilmTest() throws Exception {
        // Подготовка
        String urlString = "/film";
        Film film = Film
                .builder()
                .name("Pulp Fiction")
                .description("Black comedy criminal film")
                .releaseDate(LocalDate.of(1994, 10, 14))
                .duration(195)
                .build();
        String requestJson = gson.toJson(film);
        mockMvc.perform(post(urlString).contentType(APPLICATION_JSON_UTF8).content(requestJson));
        // Исполнение
        Film expectedFilm = Film
                .builder()
                .id(1)
                .name("Pulp Fiction")
                .description("Second film by Quentin Tarantino.")
                .releaseDate(LocalDate.of(1994, 10, 14))
                .duration(195)
                .build();
        requestJson = gson.toJson(expectedFilm);
        MvcResult result = mockMvc.perform(put(urlString).contentType(APPLICATION_JSON_UTF8).content(requestJson)).andReturn();
        String response = result.getResponse().getContentAsString();
        Film actualFilm = gson.fromJson(response, Film.class);
        // Проверка
        assertEquals(expectedFilm, actualFilm);
    }
}