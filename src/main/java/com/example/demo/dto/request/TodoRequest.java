package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TodoRequest {
    @NotBlank(message = "Title is required")
    private String title ;

    private String description ;
    private boolean completed ;
}
