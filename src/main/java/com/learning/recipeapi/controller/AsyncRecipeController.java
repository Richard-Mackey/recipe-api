package com.learning.recipeapi.controller;

import com.learning.recipeapi.Category;
import com.learning.recipeapi.entity.Recipe;
import com.learning.recipeapi.service.RecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/async")
public class AsyncRecipeController {
  private final RecipeService recipeService;

  @Autowired
  public AsyncRecipeController(RecipeService recipeService) {
    this.recipeService = recipeService;
  }

  @GetMapping("/recipes")
  public CompletableFuture<Page<Recipe>> getAllRecipesAsync(Pageable pageable) {
    return recipeService.getAllRecipesAsync(pageable);
  }

  @GetMapping("/recipes/{id}")
  public CompletableFuture<Recipe> getRecipeByIdAsync(@PathVariable Integer id) {
    return recipeService.getRecipeByIdAsync(id);
  }

  @GetMapping("/recipes/search")
  public CompletableFuture<List<Recipe>> searchRecipesAsync(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String ingredient) {
    if (name != null) {
      return recipeService.getRecipeByIngredientAsync(name);
    }
    if (ingredient != null) {
      return recipeService.getRecipesByNameAsync(ingredient);
    }
    return CompletableFuture.completedFuture(List.of());
  }

  @GetMapping("/recipes/category/{category}")
  public CompletableFuture<List<Recipe>> getRecipesByCategoryAsync(
      @PathVariable Category category) {
    return recipeService.getRecipeByCategoryAsync(category);
  }

  @GetMapping("/recipes/quick")
  public CompletableFuture<List<Recipe>> getRecipesLessThanThirtyMinsAsync(
      @RequestParam(defaultValue = "30") Integer max) {
    return recipeService.getRecipeByPrepTimeLessThanAsync(max);
  }

  @GetMapping("/recipes/servings/{count}")
  public CompletableFuture<List<Recipe>> getRecipesByServingsAsync(@PathVariable Integer count) {
    return recipeService.getRecipesByServingsAsync(count);
  }

  @PostMapping("/recipes")
  public CompletableFuture<Recipe> createRecipeAsync(@RequestBody Recipe recipe) {
    return recipeService.createRecipeAsync(recipe);
  }

  @PutMapping("/recipes/{id}")
  public CompletableFuture<Recipe> updateRecipeAsync(
      @RequestBody Recipe recipe, @PathVariable Integer id) {
    return recipeService.updateRecipeAsync(id, recipe);
  }
}
