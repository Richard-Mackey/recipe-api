package com.learning.recipeapi.exception;

import org.springframework.http.HttpStatus;

// All exceptions will inherit this structure
public class ApiException extends RuntimeException {

  // each exception provides an HTTP status and error code (eg 'Task not found')
  private final HttpStatus status;
  private final String errorCode;

  public ApiException(String message, HttpStatus status, String errorCode) {
    super(message);
    this.status = status;
    this.errorCode = errorCode;
  }

  public ApiException(String message, HttpStatus status) {
    this(message, status, null);
  }

  public HttpStatus getStatus() {
    return status;
  }

  public String getErrorCode() {
    return errorCode;
  }
}
