package com.learning.recipeapi.service;

import com.learning.recipeapi.*;
import com.learning.recipeapi.entity.Recipe;
import com.learning.recipeapi.entity.User;
import com.learning.recipeapi.exception.DuplicateRecipeException;
import com.learning.recipeapi.exception.InvalidPrepTimeException;
import com.learning.recipeapi.exception.RecipeNotFoundException;
import com.learning.recipeapi.repository.IngredientRepository;
import com.learning.recipeapi.repository.RecipeRepository;
import com.learning.recipeapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class RecipeService {
  private final RecipeRepository recipeRepository;
  private static final Logger logger = LoggerFactory.getLogger(RecipeService.class);
  private final UserRepository userRepository;
  private final IngredientRepository ingredientRepository;

  @Autowired
  public RecipeService(
      RecipeRepository recipeRepository,
      IngredientRepository ingredientRepository,
      UserRepository userRepository) {
    this.recipeRepository = recipeRepository;
    this.userRepository = userRepository;
    this.ingredientRepository = ingredientRepository;
  }

  public Page<Recipe> getAllRecipes(Pageable pageable) {

    logger.debug(
        "Fetching all recipes with pagination: page={}, size={}",
        pageable.getPageNumber(),
        pageable.getPageSize());

    Page<Recipe> recipes = recipeRepository.findAll(pageable);

    logger.info(
        ("Retrieved {} recipes (page {} of {}"),
        recipes.getNumberOfElements(),
        recipes.getNumber() + 1,
        recipes.getTotalPages());

    return recipes;
  }

  public Recipe getRecipeById(int id) {
    logger.debug("Fetching recipe with id: {}", id);
    return recipeRepository
        .findById(id)
        .map(
            recipe -> {
              logger.info("Found recipe: {}", recipe.getName());
              return recipe;
            })
        .orElseThrow(
            () -> {
              logger.error("Recipe not found with id: {}", id);
              return new RecipeNotFoundException("Recipe not found with Id: " + id);
            });
  }

  public List<Recipe> getRecipesByName(String name) {
    return recipeRepository.findByNameContainingIgnoreCase(name);
  }

  public List<Recipe> getRecipeByIngredient(String ingredient) {
    return recipeRepository.findByIngredientsTextContainingIgnoreCase(ingredient);
  }

  public List<Recipe> getRecipeByCategory(Category category) {
    return recipeRepository.findByCategory(category);
  }

  public List<Recipe> getRecipeByPrepTimeLessThan(Integer min) {
    return recipeRepository.findByPrepTimeMinutesLessThan(min);
  }

  public List<Recipe> getRecipesByServings(Integer servings) {
    return recipeRepository.findByServings(servings);
  }

  public List<Recipe> getRecipesByPrepTimeRange(Integer min, Integer max) {
    return recipeRepository.findByPrepTimeMinutesBetween(min, max);
  }

  public Recipe createRecipe(Recipe recipe) {
    logger.info("Creating recipe: {}", recipe.getName());

    Integer prepTime = recipe.getPrepTimeMinutes();

    // Check if prep time is negative
    if (prepTime != null && prepTime < 0) {
      logger.warn("Attempted to create recipe with negative prep time");
      throw new InvalidPrepTimeException(prepTime);
    }

    // Check if prep time is unreasonably high (more than 24 hours)
    if (prepTime != null && prepTime > 1440) {
      logger.warn("Attempted to create recipe with prep time greater than 1440");
      throw new InvalidPrepTimeException(prepTime);
    }
    List<Recipe> existingRecipes =
        recipeRepository.findByNameContainingIgnoreCase(recipe.getName());

    if (!existingRecipes.isEmpty()) {
      logger.warn("Attempted to create duplicate recipe: {}", recipe.getName());
      throw new DuplicateRecipeException(recipe.getName());
    }
    // Get authenticated user
    String username = getAuthenticatedUsername();
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new IllegalStateException("User not found: " + username));

    // Associate recipe with user
    recipe.setUser(user);
    Recipe savedRecipe = recipeRepository.save(recipe);
    logger.info("Created recipe with id: {}", savedRecipe.getId());
    return savedRecipe;
  }

  public Recipe updateRecipe(Integer id, Recipe updateRecipe) {
    Recipe existingRecipe = getRecipeById(id);

    validateRecipeOwnership(existingRecipe);

    existingRecipe.setName(updateRecipe.getName());
    existingRecipe.setIngredients(updateRecipe.getIngredients());
    existingRecipe.setPrepTimeMinutes(updateRecipe.getPrepTimeMinutes());
    existingRecipe.setServings(updateRecipe.getServings());
    existingRecipe.setCategory(updateRecipe.getCategory());
    existingRecipe.setInstructions(updateRecipe.getInstructions());
    existingRecipe.setDescription(updateRecipe.getDescription());
    return recipeRepository.save(existingRecipe);
  }

  public void deleteRecipe(Integer id) {
    logger.info("Deleting recipe with id: {}", id);

    Recipe existingRecipe = getRecipeById(id);
    validateRecipeOwnership(existingRecipe);

    recipeRepository.deleteById(id);
    logger.info("Deleted recipe with id: {}", id);
  }

  public CompletableFuture<Page<Recipe>> getAllRecipesAsync(Pageable pageable) {
    return CompletableFuture.supplyAsync(() -> getAllRecipes(pageable));
  }

  public CompletableFuture<Recipe> getRecipeByIdAsync(Integer id) {
    return CompletableFuture.supplyAsync(() -> getRecipeById(id));
  }

  public CompletableFuture<List<Recipe>> getRecipesByNameAsync(String name) {
    return CompletableFuture.supplyAsync(() -> getRecipesByName(name));
  }

  public CompletableFuture<List<Recipe>> getRecipeByIngredientAsync(String ingredient) {
    return CompletableFuture.supplyAsync(() -> getRecipeByIngredient(ingredient));
  }

  public CompletableFuture<List<Recipe>> getRecipeByCategoryAsync(Category category) {
    return CompletableFuture.supplyAsync(() -> getRecipeByCategory(category));
  }

  public CompletableFuture<List<Recipe>> getRecipeByPrepTimeLessThanAsync(Integer min) {
    return CompletableFuture.supplyAsync(() -> getRecipeByPrepTimeLessThan(min));
  }

  public CompletableFuture<List<Recipe>> getRecipesByServingsAsync(Integer servings) {
    return CompletableFuture.supplyAsync(() -> getRecipesByServings(servings));
  }

  public CompletableFuture<List<Recipe>> getFindByPrepTimeMinutesBetweenAsync(
      Integer min, Integer max) {
    return CompletableFuture.supplyAsync(() -> getRecipesByPrepTimeRange(min, max));
  }

  public CompletableFuture<Recipe> createRecipeAsync(Recipe recipe) {
    return CompletableFuture.supplyAsync(() -> createRecipe(recipe));
  }

  public CompletableFuture<Recipe> updateRecipeAsync(Integer id, Recipe recipe) {
    return CompletableFuture.supplyAsync(() -> updateRecipe(id, recipe));
  }

  private String getAuthenticatedUsername() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      throw new IllegalStateException("No authenticated user found");
    }
    return authentication.getName();
  }

  private void validateRecipeOwnership(Recipe recipe) {
    String authenticatedUsername = getAuthenticatedUsername();

    if (!recipe.getUser().getUsername().equals(authenticatedUsername)) {
      throw new IllegalArgumentException("You can only modify your own recipes");
    }
  }
}
