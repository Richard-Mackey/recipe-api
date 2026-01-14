package com.learning.recipeapi.dto;

import java.util.List;

public record SpoonacularIngredient(
    // JSON structure returns extendedIngredients array of ingredient objects. Original is the field
    // needed
    String original) {}
