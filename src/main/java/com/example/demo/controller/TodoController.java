package com.example.demo.controller;

import com.example.demo.dto.request.TodoRequest;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.TodoResponse;
import com.example.demo.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<TodoResponse> createTodo(@Valid @RequestBody TodoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(todoService.createTodo(request));
    }

    @GetMapping
    public ResponseEntity<PaginatedResponse<TodoResponse>> getTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDir,
            @RequestParam(required = false) Boolean completed) {
        return ResponseEntity.ok(todoService.getTodos(page, limit, sortBy, sortDir, completed));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TodoResponse> updateTodo(@PathVariable Long id, @Valid @RequestBody TodoRequest request) {
        return ResponseEntity.ok(todoService.updateTodo(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build(); // Returns 204
    }
}
