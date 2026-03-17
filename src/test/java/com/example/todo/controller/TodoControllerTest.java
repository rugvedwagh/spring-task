package com.example.todo.controller;

import com.example.todo.config.JwtService;
import com.example.todo.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TodoControllerTest {

    @Autowired MockMvc mvc;
    @Autowired TodoRepository repo;
    @Autowired JwtService jwtService;

    private String token;

    @BeforeEach
    void setUp() {
        repo.deleteAll();
        token = "Bearer " + jwtService.generateToken("test@test.com");
    }

    @Test
    void createTodo() throws Exception {
        mvc.perform(post("/todos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Buy milk\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Buy milk"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void getAllTodos() throws Exception {
        mvc.perform(post("/todos").header("Authorization", token).contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Task 1\"}"));
        mvc.perform(post("/todos").header("Authorization", token).contentType(MediaType.APPLICATION_JSON).content("{\"title\":\"Task 2\"}"));

        mvc.perform(get("/todos").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getTodoById() throws Exception {
        String response = mvc.perform(post("/todos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Find me\"}"))
                .andReturn().getResponse().getContentAsString();

        Long id = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(response).get("id").asLong();

        mvc.perform(get("/todos/" + id).header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Find me"));
    }

    @Test
    void updateTodo() throws Exception {
        String response = mvc.perform(post("/todos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Old title\"}"))
                .andReturn().getResponse().getContentAsString();

        Long id = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(response).get("id").asLong();

        mvc.perform(put("/todos/" + id)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New title\",\"completed\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New title"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void deleteTodo() throws Exception {
        String response = mvc.perform(post("/todos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Delete me\"}"))
                .andReturn().getResponse().getContentAsString();

        Long id = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(response).get("id").asLong();

        mvc.perform(delete("/todos/" + id).header("Authorization", token))
                .andExpect(status().isNoContent());

        mvc.perform(get("/todos/" + id).header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateNonExistentTodo() throws Exception {
        mvc.perform(put("/todos/999")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Ghost\",\"completed\":false}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteNonExistentTodo() throws Exception {
        mvc.perform(delete("/todos/999").header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    void createTodoWithEmptyTitle() throws Exception {
        mvc.perform(post("/todos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Title cannot be empty"));
    }

    @Test
    void createTodoWithTitleTooLong() throws Exception {
        String longTitle = "a".repeat(101);
        mvc.perform(post("/todos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"" + longTitle + "\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Title cannot exceed 100 characters"));
    }

    @Test
    void createdAtIsSetOnCreate() throws Exception {
        mvc.perform(post("/todos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Timestamp test\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void updatedAtChangesOnUpdate() throws Exception {
        String response = mvc.perform(post("/todos")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Original\"}"))
                .andReturn().getResponse().getContentAsString();

        Long id = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(response).get("id").asLong();

        Thread.sleep(100);

        String updated = mvc.perform(put("/todos/" + id)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated\",\"completed\":true}"))
                .andReturn().getResponse().getContentAsString();

        com.fasterxml.jackson.databind.JsonNode updatedNode =
                new com.fasterxml.jackson.databind.ObjectMapper().readTree(updated);

        String createdAt = updatedNode.get("createdAt").asText();
        String updatedAt = updatedNode.get("updatedAt").asText();

        assert !createdAt.equals(updatedAt) : "updatedAt should differ from createdAt after update";
    }
}