package com.learning.recipeapi.exception;

public class DuplicateRecipeException extends RuntimeException {
  public DuplicateRecipeException(String name) {
    super("Recipe with name " + name + " already exists");
  }
}
