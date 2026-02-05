package com.learning.recipeapi.dto;

import java.util.List;

public record SpoonacularRecipeDetailDTO(
    Integer id,
    String title,
    String image,
    Integer servings,
    Integer readyInMinutes,
    List<SpoonacularIngredient> extendedIngredients,
    List<SpoonacularRecipeInstructionGroup> analyzedInstructions,
    List<String> dishTypes) {}
