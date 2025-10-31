package org.example.library.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ErrorResponse {

    private String error;
    private String message;
    private LocalDateTime timestamp;
    private List<String> details;
}
