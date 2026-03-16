package com.example.todo.service;

import com.example.todo.dto.TodoRequestDto;
import com.example.todo.dto.TodoResponseDto;

import java.util.List;

public interface TodoService {
    List<TodoResponseDto> getAll();
    TodoResponseDto getById(Long id);
    TodoResponseDto create(TodoRequestDto request);
    TodoResponseDto update(Long id, TodoRequestDto request);
    void delete(Long id);
}