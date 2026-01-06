package com.learning.recipeapi.exception;

public class RecipeNotFoundException extends RuntimeException {
  public RecipeNotFoundException(String message) {
    super(message);
  }

  public RecipeNotFoundException(Integer id) {
    super("Recipe not found with id " + id);
  }
}
