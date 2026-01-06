package com.learning.recipeapi;

import com.learning.recipeapi.entity.Recipe;
import com.learning.recipeapi.repository.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class RecipeRepositoryTest {
  @Autowired private RecipeRepository recipeRepository;

  // Helper method
  private Recipe createRecipe(
      String name,
      String description,
      String ingredients,
      String instructions,
      Category category,
      Integer prepTimeMinutes,
      Integer servings) {
    Recipe recipe = new Recipe();
    recipe.setName(name);
    recipe.setDescription(description);
    recipe.setIngredientsText(ingredients);
    recipe.setInstructions(instructions);
    recipe.setCategory(category);
    recipe.setPrepTimeMinutes(prepTimeMinutes);
    recipe.setServings(servings);
    return recipe;
  }

  @Test
  void testSaveRecipe() {
    Recipe recipe =
        this.createRecipe(
            "Chicken stew",
            "stew",
            "Chicken, onions, stock",
            "Fry chicken, and onions before adding stock",
            Category.DINNER,
            30,
            4);

    Recipe savedRecipe = this.recipeRepository.save(recipe);

    assertNotNull(savedRecipe.getId());
    assertEquals("Chicken stew", savedRecipe.getName());
    assertEquals("Chicken, onions, stock", savedRecipe.getIngredientsText());
    assertEquals(30, savedRecipe.getPrepTimeMinutes());
  }

  @Test
  void testFindRecipeById() {
    Recipe recipe1 =
        this.createRecipe(
            "Chicken stew",
            "stew",
            "Chicken, onions, stock",
            "Fry chicken, and onions before adding stock",
            Category.DINNER,
            30,
            4);

    Recipe saved = recipeRepository.save(recipe1);
    Optional<Recipe> found = recipeRepository.findById(saved.getId());

    assertTrue(found.isPresent());
    assertEquals("Chicken stew", found.get().getName());
    assertEquals("Chicken, onions, stock", found.get().getIngredientsText());
  }

  @Test
  void testFindAll() {
    recipeRepository.save(
        createRecipe(
            "Chicken stew",
            "stew",
            "Chicken, onions, stock",
            "Fry chicken, and onions before adding stock",
            Category.DINNER,
            30,
            4));
    recipeRepository.save(
        createRecipe(
            "Beef curry",
            "curry",
            "Beef, onions, curry powder, stock",
            "Fry beef, and onions before adding curry powder and stock",
            Category.DINNER,
            60,
            6));
    recipeRepository.save(
        createRecipe(
            "Beans on toast",
            "snack",
            "Beans , bread, butter",
            "Heat beans, put toast under grill, add butter to taste",
            Category.LUNCH,
            10,
            2));
    List<Recipe> allRecipes = this.recipeRepository.findAll();
    assertEquals(3, allRecipes.size());
  }

  @Test
  void deleteRecipe() {
    Recipe recipe =
        createRecipe(
            "Beans on toast",
            "snack",
            "Beans , bread, butter",
            "Heat beans, put toast under grill, add butter to taste",
            Category.LUNCH,
            10,
            2);
    Recipe savedRecipe = recipeRepository.save(recipe);
    recipeRepository.delete(savedRecipe);

    Optional<Recipe> result = recipeRepository.findById(savedRecipe.getId());
    assertTrue(result.isEmpty());
  }

  @Test
  void testFindByNameIgnoreCase() {
    recipeRepository.save(
        createRecipe(
            "Chicken stew",
            "stew",
            "Chicken, onions, stock",
            "Fry chicken, and onions before adding stock",
            Category.DINNER,
            30,
            4));
    recipeRepository.save(
        createRecipe(
            "Beef curry",
            "curry",
            "Beef, onions, curry powder, stock",
            "Fry beef, and onions before adding curry powder and stock",
            Category.DINNER,
            60,
            6));
    recipeRepository.save(
        createRecipe(
            "Beans on toast",
            "snack",
            "Beans , bread, butter",
            "Heat beans, put toast under grill, add butter to taste",
            Category.LUNCH,
            10,
            2));
    List<Recipe> found = recipeRepository.findByNameContainingIgnoreCase("BEEF");

    assertEquals(1, found.size());
  }

  @Test
  void testFindByIngredientsContainingIgnoreCase() {
    recipeRepository.save(
        createRecipe(
            "Chicken stew",
            "stew",
            "Chicken, onions, stock",
            "Fry chicken, and onions before adding stock",
            Category.DINNER,
            30,
            4));
    recipeRepository.save(
        createRecipe(
            "Beef curry",
            "curry",
            "Beef, onions, curry powder, stock",
            "Fry beef, and onions before adding curry powder and stock",
            Category.DINNER,
            60,
            6));
    recipeRepository.save(
        createRecipe(
            "Beans on toast",
            "snack",
            "Beans , bread, butter",
            "Heat beans, put toast under grill, add butter to taste",
            Category.LUNCH,
            10,
            2));

    List<Recipe> found = recipeRepository.findByIngredientsTextContainingIgnoreCase("ONIONS");
    assertEquals(2, found.size());
  }

  @Test
  void testFindByCategory() {
    recipeRepository.save(
        createRecipe(
            "Chicken stew",
            "stew",
            "Chicken, onions, stock",
            "Fry chicken, and onions before adding stock",
            Category.DINNER,
            30,
            4));
    recipeRepository.save(
        createRecipe(
            "Beef curry",
            "curry",
            "Beef, onions, curry powder, stock",
            "Fry beef, and onions before adding curry powder and stock",
            Category.DINNER,
            60,
            6));
    recipeRepository.save(
        createRecipe(
            "Beans on toast",
            "snack",
            "Beans , bread, butter",
            "Heat beans, put toast under grill, add butter to taste",
            Category.LUNCH,
            10,
            2));
    List<Recipe> found = recipeRepository.findByCategory(Category.LUNCH);
    assertEquals(1, found.size());
  }

  @Test
  void testFindByPrepTimeMinutesLessThan() {
    recipeRepository.save(
        createRecipe(
            "Chicken stew",
            "stew",
            "Chicken, onions, stock",
            "Fry chicken, and onions before adding stock",
            Category.DINNER,
            30,
            4));
    recipeRepository.save(
        createRecipe(
            "Beef curry",
            "curry",
            "Beef, onions, curry powder, stock",
            "Fry beef, and onions before adding curry powder and stock",
            Category.DINNER,
            60,
            6));
    recipeRepository.save(
        createRecipe(
            "Beans on toast",
            "snack",
            "Beans , bread, butter",
            "Heat beans, put toast under grill, add butter to taste",
            Category.LUNCH,
            10,
            2));
    List<Recipe> found = recipeRepository.findByPrepTimeMinutesLessThan(15);
    assertEquals(1, found.size());
  }

  @Test
  void testFindByServings() {
    recipeRepository.save(
        createRecipe(
            "Chicken stew",
            "stew",
            "Chicken, onions, stock",
            "Fry chicken, and onions before adding stock",
            Category.DINNER,
            30,
            4));
    recipeRepository.save(
        createRecipe(
            "Beef curry",
            "curry",
            "Beef, onions, curry powder, stock",
            "Fry beef, and onions before adding curry powder and stock",
            Category.DINNER,
            60,
            6));
    recipeRepository.save(
        createRecipe(
            "Beans on toast",
            "snack",
            "Beans , bread, butter",
            "Heat beans, put toast under grill, add butter to taste",
            Category.LUNCH,
            10,
            2));
    List<Recipe> found = recipeRepository.findByServings(4);
    assertEquals(1, found.size());
  }

  @Test
  void testFindByPrepTimeMinutesBetween() {
    recipeRepository.save(
        createRecipe(
            "Chicken stew",
            "stew",
            "Chicken, onions, stock",
            "Fry chicken, and onions before adding stock",
            Category.DINNER,
            30,
            4));
    recipeRepository.save(
        createRecipe(
            "Beef curry",
            "curry",
            "Beef, onions, curry powder, stock",
            "Fry beef, and onions before adding curry powder and stock",
            Category.DINNER,
            60,
            6));
    recipeRepository.save(
        createRecipe(
            "Beans on toast",
            "snack",
            "Beans , bread, butter",
            "Heat beans, put toast under grill, add butter to taste",
            Category.LUNCH,
            10,
            2));
    List<Recipe> found = recipeRepository.findByPrepTimeMinutesBetween(5, 40);
    assertEquals(2, found.size());
  }
}
