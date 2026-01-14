package com.learning.recipeapi.controller;

import com.learning.recipeapi.Category;
import com.learning.recipeapi.dto.SpoonacularSearchResponse;
import com.learning.recipeapi.entity.Recipe;
import com.learning.recipeapi.entity.User;
import com.learning.recipeapi.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
public class RecipeController {
  private final RecipeService recipeService;
  private static final Logger logger = LoggerFactory.getLogger(RecipeController.class);

  @Autowired
  public RecipeController(RecipeService recipeService) {
    this.recipeService = recipeService;
  }

  @GetMapping("/recipes")
  public Page<Recipe> getAllRecipes(Pageable pageable) {
    logger.info(
        "GET /recipes - Request received (page={}, size={})",
        pageable.getPageNumber(),
        pageable.getPageSize());

    return recipeService.getAllRecipes(pageable);
  }

  @GetMapping("/recipes/{id}")
  public Recipe getRecipeById(@PathVariable Integer id) {
    return recipeService.getRecipeById(id);
  }

  @GetMapping("/recipes/search")
  public List<Recipe> searchRecipe(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String ingredient) {
    if (name != null) {
      return recipeService.getRecipesByName(name);
    }
    if (ingredient != null) {
      return recipeService.getRecipeByIngredient(ingredient);
    }
    return List.of();
  }

  @GetMapping("/recipes/category/{category}")
  public List<Recipe> getRecipesByCategory(@PathVariable Category category) {
    return recipeService.getRecipeByCategory(category);
  }

  @GetMapping("/recipes/quick")
  public List<Recipe> getRecipesLessThanThirtyMins(@RequestParam(defaultValue = "30") Integer max) {
    if (max != null) {
      return recipeService.getRecipeByPrepTimeLessThan(max);
    }
    return List.of();
  }

  @GetMapping("/recipes/servings/{count}")
  public List<Recipe> getRecipesByServings(@PathVariable Integer count) {
    return recipeService.getRecipesByServings(count);
  }

  @PostMapping("/recipes")
  public ResponseEntity<Recipe> createRecipe(@Valid @RequestBody Recipe recipe) {
    logger.info("POST /recipes - Creating recipe: {}", recipe.getName());

    Recipe created = recipeService.createRecipe(recipe);

    logger.info("POST /recipes - Created recipe with id: {}", created.getId());

    return new ResponseEntity<>(created, HttpStatus.CREATED);
  }

  @DeleteMapping("/recipes/{id}")
  public ResponseEntity<Void> deleteRecipe(@PathVariable Integer id) {
    recipeService.deleteRecipe(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/recipes/{id}")
  public Recipe updateRecipe(@PathVariable int id, @Valid @RequestBody Recipe recipe) {
    return recipeService.updateRecipe(id, recipe);
  }

  @GetMapping("/recipes/search/spoonacular")
  public ResponseEntity<SpoonacularSearchResponse> searchSpoonacularRecipes(
      @RequestParam String query,
      @RequestParam(defaultValue = "5") Integer number,
      @AuthenticationPrincipal User user) {

    SpoonacularSearchResponse response =
        recipeService.searchSpoonacularRecipes(query, user, number);
    return ResponseEntity.ok(response);
  }
  @PostMapping("/recipes/spoonacular/{spoonacularId}")
  public ResponseEntity<Recipe> saveSpoonacularRecipe(
          @PathVariable Integer spoonacularId,
          @AuthenticationPrincipal User user
  ) {
    logger.info("POST /recipes/spoonacular/{} - Saving Spoonacular recipe for user: {}",
            spoonacularId, user.getUsername());

    Recipe saved = recipeService.saveSpoonacularRecipe(spoonacularId, user);

    logger.info("Saved Spoonacular recipe with id: {}", saved.getId());

    return new ResponseEntity<>(saved, HttpStatus.CREATED);
  }}
