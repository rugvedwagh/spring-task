package com.example.todo.repository;

import com.example.todo.model.Todo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class TodoRepositoryTest {

    @Autowired
    TodoRepository repo;

    private Todo todo;

    @BeforeEach
    void setUp() {
        repo.deleteAll();
        todo = new Todo();
        todo.setTitle("Buy milk");
        todo.setCompleted(false);
    }

    @Test
    void save_persistsTodo() {
        Todo saved = repo.save(todo);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Buy milk");
        assertThat(saved.isCompleted()).isFalse();
    }

    @Test
    void findById_returnsTodo_whenExists() {
        Todo saved = repo.save(todo);

        Optional<Todo> found = repo.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Buy milk");
    }

    @Test
    void findById_returnsEmpty_whenNotExists() {
        Optional<Todo> found = repo.findById(999L);

        assertThat(found).isEmpty();
    }

    @Test
    void findAll_returnsAllTodos() {
        Todo todo2 = new Todo();
        todo2.setTitle("Walk dog");
        repo.save(todo);
        repo.save(todo2);

        List<Todo> todos = repo.findAll();

        assertThat(todos).hasSize(2);
    }

    @Test
    void delete_removesTodo() {
        Todo saved = repo.save(todo);

        repo.deleteById(saved.getId());

        assertThat(repo.findById(saved.getId())).isEmpty();
    }

    @Test
    void update_changesTodoFields() {
        Todo saved = repo.save(todo);
        saved.setTitle("Updated title");
        saved.setCompleted(true);
        repo.save(saved);

        Todo updated = repo.findById(saved.getId()).get();

        assertThat(updated.getTitle()).isEqualTo("Updated title");
        assertThat(updated.isCompleted()).isTrue();
    }

    @Test
    void count_returnsCorrectNumber() {
        repo.save(todo);

        Todo todo2 = new Todo();
        todo2.setTitle("Walk dog");
        repo.save(todo2);

        assertThat(repo.count()).isEqualTo(2);
    }
}