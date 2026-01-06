package com.learning.recipeapi;

import com.learning.recipeapi.Category;
import com.learning.recipeapi.entity.Ingredient;
import com.learning.recipeapi.entity.Recipe;
import com.learning.recipeapi.repository.IngredientRepository;
import com.learning.recipeapi.repository.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class IngredientRepositoryTest {

  @Autowired private RecipeRepository recipeRepository;

  @Autowired private IngredientRepository ingredientRepository;

  @Test
  void testSaveRecipeWithIngredients_UsingHelperMethod() {
    // Arrange - create recipe
    Recipe recipe = new Recipe();
    recipe.setName("Spaghetti Carbonara");
    recipe.setDescription("Classic Italian pasta");
    recipe.setIngredientsText("Pasta, eggs, bacon, parmesan");
    recipe.setInstructions("Cook pasta, mix eggs and cheese, combine");
    recipe.setPrepTimeMinutes(30);
    recipe.setServings(4);
    recipe.setCategory(Category.DINNER);

    // Create ingredients
    Ingredient pasta = new Ingredient("Pasta");
    Ingredient eggs = new Ingredient("Eggs");
    Ingredient bacon = new Ingredient("Bacon");
    Ingredient parmesan = new Ingredient("Parmesan");

    // Use helper method to establish relationship
    recipe.addIngredient(pasta);
    recipe.addIngredient(eggs);
    recipe.addIngredient(bacon);
    recipe.addIngredient(parmesan);

    // Act - save recipe (ingredients should cascade)
    Recipe savedRecipe = recipeRepository.save(recipe);

    // Assert - verify recipe saved
    assertNotNull(savedRecipe.getId());
    assertEquals("Spaghetti Carbonara", savedRecipe.getName());

    // Verify ingredients were cascaded and saved
    assertEquals(4, savedRecipe.getIngredients().size());

    // Verify ingredients have IDs (proof they were saved)
    assertNotNull(pasta.getId());
    assertNotNull(eggs.getId());
    assertNotNull(bacon.getId());
    assertNotNull(parmesan.getId());

    // Verify bidirectional relationship works
    assertTrue(pasta.getRecipes().contains(savedRecipe));
    assertTrue(eggs.getRecipes().contains(savedRecipe));
  }

  @Test
  void testManyToMany_OneIngredientInMultipleRecipes() {
    // Arrange - create shared ingredient
    Ingredient parmesan = new Ingredient("Parmesan");

    // Create two different recipes
    Recipe carbonara = new Recipe();
    carbonara.setName("Carbonara");
    carbonara.setIngredientsText("Pasta, eggs, bacon, parmesan");
    carbonara.setInstructions("Make carbonara");
    carbonara.setPrepTimeMinutes(30);
    carbonara.setServings(4);
    carbonara.setCategory(Category.DINNER);

    Recipe caesarSalad = new Recipe();
    caesarSalad.setName("Caesar Salad");
    caesarSalad.setIngredientsText("Lettuce, parmesan, croutons");
    caesarSalad.setInstructions("Make salad");
    caesarSalad.setPrepTimeMinutes(15);
    caesarSalad.setServings(2);
    caesarSalad.setCategory(Category.LUNCH);

    // Add same ingredient to both recipes
    carbonara.addIngredient(parmesan);
    caesarSalad.addIngredient(parmesan);

    // Act - save both recipes
    recipeRepository.save(carbonara);
    recipeRepository.save(caesarSalad);

    // Assert - parmesan appears in both recipes
    assertEquals(2, parmesan.getRecipes().size());
    assertTrue(parmesan.getRecipes().contains(carbonara));
    assertTrue(parmesan.getRecipes().contains(caesarSalad));

    // Verify only ONE parmesan ingredient exists
    Ingredient foundParmesan = ingredientRepository.findById(parmesan.getId()).orElse(null);
    assertNotNull(foundParmesan);
    assertEquals(2, foundParmesan.getRecipes().size());
  }

  @Test
  void testFindRecipeAndNavigateToIngredients() {
    // Arrange - create recipe with ingredients
    Recipe recipe = new Recipe();
    recipe.setName("Pancakes");
    recipe.setIngredientsText("Flour, eggs, milk");
    recipe.setInstructions("Mix and cook");
    recipe.setPrepTimeMinutes(20);
    recipe.setServings(4);
    recipe.setCategory(Category.BREAKFAST);

    Ingredient flour = new Ingredient("Flour");
    Ingredient eggs = new Ingredient("Eggs");
    Ingredient milk = new Ingredient("Milk");

    recipe.addIngredient(flour);
    recipe.addIngredient(eggs);
    recipe.addIngredient(milk);

    Recipe savedRecipe = recipeRepository.save(recipe);

    // Act - retrieve recipe by ID
    Recipe foundRecipe = recipeRepository.findById(savedRecipe.getId()).orElse(null);

    // Assert - verify we can navigate from recipe to ingredients
    assertNotNull(foundRecipe);
    assertEquals("Pancakes", foundRecipe.getName());
    assertEquals(3, foundRecipe.getIngredients().size());

    // Verify ingredient names
    List<String> ingredientNames =
        foundRecipe.getIngredients().stream().map(Ingredient::getName).toList();
    assertTrue(ingredientNames.contains("Flour"));
    assertTrue(ingredientNames.contains("Eggs"));
    assertTrue(ingredientNames.contains("Milk"));
  }
}
