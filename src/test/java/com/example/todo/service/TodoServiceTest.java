package com.example.todo.service;

import com.example.todo.dto.TodoRequestDto;
import com.example.todo.dto.TodoResponseDto;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.model.Todo;
import com.example.todo.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    TodoRepository repo;

    @InjectMocks
    TodoServiceImpl service;

    private Todo todo;
    private TodoRequestDto request;

    @BeforeEach
    void setUp() {
        todo = new Todo();
        todo.setTitle("Buy milk");

        request = new TodoRequestDto();
        request.setTitle("Buy milk");
        request.setCompleted(false);
    }

    @Test
    void getAll_returnsMappedList() {
        when(repo.findAll()).thenReturn(List.of(todo));

        List<TodoResponseDto> result = service.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Buy milk");
    }

    @Test
    void getById_returnsDto_whenFound() {
        when(repo.findById(1L)).thenReturn(Optional.of(todo));

        TodoResponseDto result = service.getById(1L);

        assertThat(result.getTitle()).isEqualTo("Buy milk");
    }

    @Test
    void getById_throwsException_whenNotFound() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(TodoNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_savesAndReturnsDto() {
        when(repo.save(any(Todo.class))).thenReturn(todo);

        TodoResponseDto result = service.create(request);

        assertThat(result.getTitle()).isEqualTo("Buy milk");
        verify(repo, times(1)).save(any(Todo.class));
    }

    @Test
    void update_updatesAndReturnsDto_whenFound() {
        when(repo.findById(1L)).thenReturn(Optional.of(todo));
        when(repo.save(any(Todo.class))).thenReturn(todo);

        request.setTitle("Updated title");
        request.setCompleted(true);

        TodoResponseDto result = service.update(1L, request);

        assertThat(result.getTitle()).isEqualTo("Updated title");
        verify(repo, times(1)).save(any(Todo.class));
    }

    @Test
    void update_throwsException_whenNotFound() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L, request))
                .isInstanceOf(TodoNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void delete_deletesSuccessfully_whenFound() {
        when(repo.findById(1L)).thenReturn(Optional.of(todo));

        service.delete(1L);

        verify(repo, times(1)).deleteById(1L);
    }

    @Test
    void delete_throwsException_whenNotFound() {
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(TodoNotFoundException.class)
                .hasMessageContaining("99");
    }
}