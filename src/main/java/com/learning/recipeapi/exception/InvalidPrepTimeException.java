package com.learning.recipeapi.exception;

public class InvalidPrepTimeException extends RuntimeException {
  public InvalidPrepTimeException(String message) {
    super(message);
  }

  public InvalidPrepTimeException(Integer prepTime) {
    super("Invalid prep time: " + prepTime + " minutes");
  }
}
