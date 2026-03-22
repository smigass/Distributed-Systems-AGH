package com.agh.student.smigas.radar.exceptions;

import com.agh.student.smigas.radar.model.response.ServerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ServerResponse<String>> handleApiException(ApiException ex) {
        ServerResponse<String> response = ServerResponse.<String>builder()
                .message(ex.getMessage())
                .status(ex.getStatus())
                .build();

        return new ResponseEntity<>(response, HttpStatus.valueOf(ex.getStatus()));
    }
}
