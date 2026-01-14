package com.learning.recipeapi.dto;

import java.util.List;

public record SpoonacularRecipeDetailDTO(
        Integer id,
        String title,
        Integer servings,           // ADD THIS
        Integer readyInMinutes,     // ADD THIS
        List<SpoonacularIngredient> extendedIngredients,
        List<SpoonacularRecipeInstructionGroup> analyzedInstructions
) {}