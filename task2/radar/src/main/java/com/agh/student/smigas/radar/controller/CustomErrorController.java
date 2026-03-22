package com.agh.student.smigas.radar.controller;

import com.agh.student.smigas.radar.exceptions.ApiException;
import com.agh.student.smigas.radar.model.response.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.webmvc.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CustomErrorController implements ErrorController {
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingParams(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest().body("Missing parameter: " + ex.getParameterName());
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ServerResponse<String>> handleApiException(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(ServerResponse.<String>builder()
                .message(ex.getMessage())
                .status(ex.getStatus())
                .build());
    }
}
