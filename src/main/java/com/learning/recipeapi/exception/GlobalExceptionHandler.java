package com.learning.recipeapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(RecipeNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleRecipeNotFoundException(
      RecipeNotFoundException ex) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("errorCode", "RECIPE_NOT_FOUND");
    errorResponse.put("error", ex.getMessage());
    errorResponse.put("status", HttpStatus.NOT_FOUND.value());
    errorResponse.put("timestamp", LocalDateTime.now());

    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(DuplicateRecipeException.class)
  public ResponseEntity<Map<String, Object>> handleDuplicateRecipeException(
      DuplicateRecipeException ex) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("errorCode", "DUPLICATE_RECIPE");
    errorResponse.put("error", ex.getMessage());
    errorResponse.put("status", HttpStatus.CONFLICT.value());
    errorResponse.put("timestamp", LocalDateTime.now());

    return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(InvalidPrepTimeException.class)
  public ResponseEntity<Map<String, Object>> handleInvalidPrepTimeException(
      InvalidPrepTimeException ex) {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("errorCode", "INVALID_PREP_TIME");
    errorResponse.put("error", ex.getMessage());
    errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
    errorResponse.put("timestamp", LocalDateTime.now());

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationErrors(
      MethodArgumentNotValidException ex) {

    Map<String, String> fieldErrors = new HashMap<>();

    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      fieldErrors.put(error.getField(), error.getDefaultMessage());
    }

    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("errorCode", "VALIDATION_FAILED");
    errorResponse.put("error", "Request validation failed");
    errorResponse.put("fieldErrors", fieldErrors);
    errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
    errorResponse.put("timestamp", LocalDateTime.now());

    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
  }
}
