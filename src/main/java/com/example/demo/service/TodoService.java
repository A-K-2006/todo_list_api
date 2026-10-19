package com.example.demo.service;

import com.example.demo.dto.request.TodoRequest;
import com.example.demo.dto.response.PaginatedResponse;
import com.example.demo.dto.response.TodoResponse;
import com.example.demo.entity.Todo;
import com.example.demo.entity.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.exception.UnauthorizedException;
import com.example.demo.repository.TodoRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public TodoResponse createTodo(TodoRequest request) {
        Todo todo = Todo.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .completed(request.isCompleted())
                .user(getCurrentUser())
                .build();
        return mapToResponse(todoRepository.save(todo));
    }

    public PaginatedResponse<TodoResponse> getTodos(int page, int limit, String sortBy, String sortDir, Boolean completed) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page - 1, limit, sort);
        Page<Todo> todoPage = todoRepository.findByUser(getCurrentUser(), completed, pageable);

        return new PaginatedResponse<>(
                todoPage.getContent().stream().map(this::mapToResponse).collect(Collectors.toList()),
                todoPage.getNumber() + 1,
                todoPage.getSize(),
                todoPage.getTotalElements(),
                todoPage.getTotalPages()
        );
    }

    public TodoResponse updateTodo(Long id, TodoRequest request) {
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Todo not found"));
        if (!todo.getUser().getId().equals(getCurrentUser().getId())) {
            throw new UnauthorizedException("Unauthorized to update this todo");
        }
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setCompleted(request.isCompleted());
        return mapToResponse(todoRepository.save(todo));
    }

    public void deleteTodo(Long id) {
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Todo not found"));
        if (!todo.getUser().getId().equals(getCurrentUser().getId())) {
            throw new UnauthorizedException("Unauthorized to delete this todo");
        }
        todoRepository.delete(todo);
    }

    private TodoResponse mapToResponse(Todo todo) {
        return new TodoResponse(todo.getId(), todo.getTitle(), todo.getDescription(), todo.isCompleted());
    }
}