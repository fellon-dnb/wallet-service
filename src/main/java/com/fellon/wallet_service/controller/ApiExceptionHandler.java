package com.fellon.wallet_service.controller;

import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    // 400 — невалидный JSON/тип поля
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleBadJson(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "invalid request body"));
    }

    // 400 — валидация @Valid на теле запроса (DTO)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        var details = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage,
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "validation failed", "details", details));
    }

    // 400 — валидация @Validated на параметрах/путях
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraint(ConstraintViolationException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "validation failed"));
    }

    // 404 или 400 — бизнес-аргументы (кошелёк не найден / прочие неверные аргументы)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArg(IllegalArgumentException e) {
        String msg = e.getMessage() == null ? "bad request" : e.getMessage();
        boolean notFound = msg.toLowerCase().startsWith("wallet not found");
        return ResponseEntity.status(notFound ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST)
                .body(Map.of("error", msg));
    }

    // 409 — недостаточно средств и прочие конфликтные состояния
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException e) {
        String msg = e.getMessage() == null ? "conflict" : e.getMessage();
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", msg));
    }

    // 500 — на всякий пожарный (не светим стектрейс наружу)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAny(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "internal error"));
    }
}
