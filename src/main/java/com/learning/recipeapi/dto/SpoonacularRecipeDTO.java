package com.learning.recipeapi.dto;

public record SpoonacularRecipeDTO(
    Integer id, String title, String image, Integer servings, Integer readyInMinutes) {}
