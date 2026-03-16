package com.example.todo.service;

import com.example.todo.dto.TodoRequestDto;
import com.example.todo.dto.TodoResponseDto;
import com.example.todo.exception.TodoNotFoundException;
import com.example.todo.model.Todo;
import com.example.todo.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoServiceImpl implements TodoService {

    private final TodoRepository repo;

    public TodoServiceImpl(TodoRepository repo) {
        this.repo = repo;
    }

    @Override
    public List<TodoResponseDto> getAll() {
        return repo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public TodoResponseDto getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Override
    public TodoResponseDto create(TodoRequestDto request) {
        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setCompleted(request.isCompleted());
        return toResponse(repo.save(todo));
    }

    @Override
    public TodoResponseDto update(Long id, TodoRequestDto request) {
        Todo todo = findOrThrow(id);
        todo.setTitle(request.getTitle());
        todo.setCompleted(request.isCompleted());
        return toResponse(repo.save(todo));
    }

    @Override
    public void delete(Long id) {
        findOrThrow(id);
        repo.deleteById(id);
    }

    // --- private helpers ---

    private Todo findOrThrow(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

    private TodoResponseDto toResponse(Todo todo) {
        return new TodoResponseDto(
                todo.getId(),
                todo.getTitle(),
                todo.isCompleted(),
                todo.getCreatedAt(),
                todo.getUpdatedAt()
        );
    }
}