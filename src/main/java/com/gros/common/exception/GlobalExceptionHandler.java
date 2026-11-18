package com.gros.common.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, String>> reponse(HttpStatus s, String msg) {
        return ResponseEntity.status(s).body(Map.of("erreur", msg));
    }

    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<Map<String, String>> nf(NotFoundException e) {
        return reponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    ResponseEntity<Map<String, String>> metier(BusinessException e) {
        return reponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<Map<String, String>> mdp(BadCredentialsException e) {
        return reponse(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ExceptionHandler(LockedException.class)
    ResponseEntity<Map<String, String>> bloque(LockedException e) {
        return reponse(HttpStatus.LOCKED, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, String>> valid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + " : " + f.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return reponse(HttpStatus.BAD_REQUEST, msg);
    }
}