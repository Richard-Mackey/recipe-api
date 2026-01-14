package com.learning.recipeapi.dto;

import java.util.List;

public record SpoonacularSearchResponse(List<SpoonacularRecipeDTO> results, Integer totalResults) {}
