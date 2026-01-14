package com.learning.recipeapi.dto;

import java.util.List;

public record SpoonacularRecipeInstructionGroup(
     String name,
     List <SpoonacularRecipeStep> steps
) {}
