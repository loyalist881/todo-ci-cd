package com.example.TODO.controller;

import com.example.TODO.dto.Todo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateValidTodo() throws Exception {
        Todo todo = new Todo();
        todo.setTitle("Spring Boot тесты");

        mockMvc.perform(post("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Spring Boot тесты"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void shouldReturnBadRequestWhenTitleIsBlank() throws Exception {
        Todo invalidTodo = new Todo();
        invalidTodo.setTitle("");

        mockMvc.perform(post("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidTodo)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnAllTodos() throws Exception {
        Todo todo = new Todo();
        todo.setTitle("Todo 1");
        mockMvc.perform(post("/todo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todo)));

        mockMvc.perform(get("/todo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Todo 1"));
    }

    @Test
    void shouldUpdateExistingTodo() throws Exception {
        Todo todo = new Todo();
        todo.setTitle("Старое название");
        mockMvc.perform(post("/todo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todo)));

        Todo updatedTodo = new Todo();
        updatedTodo.setTitle("Новое название");
        updatedTodo.setCompleted(true);

        mockMvc.perform(put("/todo/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTodo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Новое название"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentTodo() throws Exception {
        Todo updatedTodo = new Todo();
        updatedTodo.setTitle("Новое название");

        mockMvc.perform(put("/todo/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTodo)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteExistingTodo() throws Exception {
        Todo todo = new Todo();
        todo.setTitle("Todo для удаления");
        mockMvc.perform(post("/todo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(todo)));

        mockMvc.perform(delete("/todo/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/todo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturnNotFoundWhenDeletingNonExistentTodo() throws Exception {
        mockMvc.perform(delete("/todo/999"))
                .andExpect(status().isNotFound());
    }
}
