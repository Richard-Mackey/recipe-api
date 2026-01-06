package com.learning.recipeapi.service;

import com.learning.recipeapi.*;
import com.learning.recipeapi.entity.Recipe;
import com.learning.recipeapi.exception.DuplicateRecipeException;
import com.learning.recipeapi.exception.InvalidPrepTimeException;
import com.learning.recipeapi.exception.RecipeNotFoundException;
import com.learning.recipeapi.repository.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.mockito.ArgumentMatchers.any;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static java.util.Arrays.asList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {

  @Mock private RecipeRepository recipeRepository;

  @InjectMocks private RecipeService recipeService;

  // Helper method
  private Recipe createRecipe(
      Integer id,
      String name,
      String ingredientsText,
      Category category,
      Integer prepTimeMinutes,
      Integer servings) {
    Recipe recipe = new Recipe();
    recipe.setId(id);
    recipe.setName(name);
    recipe.setDescription("Test description");
    recipe.setIngredientsText(ingredientsText);
    recipe.setInstructions("Test instructions");
    recipe.setCategory(category);
    recipe.setPrepTimeMinutes(prepTimeMinutes);
    recipe.setServings(servings);
    return recipe;
  }

  @Test
  void testGetAllRecipes() {
    // Arrange
    List<Recipe> mockRecipe =
        asList(
            createRecipe(1, "Recipe1", "Ingredient1, Ingredient2", Category.DINNER, 30, 4),
            createRecipe(2, "Recipe2", "Ingredient3, Ingredient4", Category.BREAKFAST, 15, 2));
    Pageable pageable = PageRequest.of(0, 10);
    Page<Recipe> mockRecipePage = new PageImpl<>(mockRecipe,  pageable, mockRecipe.size());

    // Mock
    when(recipeRepository.findAll(pageable)).thenReturn(mockRecipePage);

    // Act
    Page<Recipe> result = recipeService.getAllRecipes(pageable);
    // Assert
    assertEquals(2, result.getContent().size());
    assertEquals("Recipe1", result.getContent().get(0).getName());
    assertEquals(0, result.getNumber()); // page number
    assertEquals(2, result.getTotalElements()); // total items
    // Verify
    verify(recipeRepository, times(1)).findAll(pageable);
  }

  @Test
  void testGetRecipeById_Success() {
    Recipe mockRecipe =
        createRecipe(1, "Recipe1", "Ingredient1, Ingredient2", Category.DINNER, 30, 4);
    when(recipeRepository.findById(1)).thenReturn(Optional.of(mockRecipe));

    Recipe result = recipeService.getRecipeById(1);

    assertNotNull(result);
    assertEquals(1, result.getId());
    assertEquals("Recipe1", result.getName());
    verify(recipeRepository, times(1)).findById(1);
  }

  @Test
  void testGetRecipeById_NotFound() {
    // Arrange - mock returns empty optional
    when(recipeRepository.findById(999)).thenReturn(Optional.empty());
    // Act and assert - exception thrown
    RecipeNotFoundException exception =
        assertThrows(RecipeNotFoundException.class, () -> recipeService.getRecipeById(999));
    // Verify message
    assertTrue(exception.getMessage().contains("Recipe not found with Id: 999"));
    // Verify repository called
    verify(recipeRepository, times(1)).findById(999);
  }

  @Test
  void testDeleteRecipe_Success() {
    // Arrange
    when(recipeRepository.existsById(1)).thenReturn(true);
    // Act
    recipeService.deleteRecipe(1);
    // Verify
    verify(recipeRepository, times(1)).existsById(1);
    verify(recipeRepository, times(1)).deleteById(1);
  }

  @Test
  void testDeleteRecipe_NotFound() {
    // Arrange
    when(recipeRepository.existsById(999)).thenReturn(false);
    // Act
    RecipeNotFoundException exception =
        assertThrows(RecipeNotFoundException.class, () -> recipeService.deleteRecipe(999));
    assertTrue(exception.getMessage().contains("Recipe not found with Id: 999"));
    // Verify
    verify(recipeRepository, times(1)).existsById(999);
    verify(recipeRepository, never()).deleteById(999);
  }

  @Test
  void testUpdateRecipe_Success() {
    // Arrange
    Recipe existingRecipe =
        createRecipe(1, "Recipe1", "Ingredient1, Ingredient2", Category.DINNER, 30, 4);
    Recipe updateRecipe =
        createRecipe(
            null, "New Recipe1", "Ingredient1, Ingredient2, Ingredient3", Category.DINNER, 40, 4);
    Recipe savedRecipe =
        createRecipe(
            1, "New Recipe1", "Ingredient1, Ingredient2, Ingredient3", Category.DINNER, 40, 4);

    // Mock findById
    when(recipeRepository.findById(1)).thenReturn(Optional.of(existingRecipe));
    // Mock save
    when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);

    // Act
    Recipe result = recipeService.updateRecipe(1, updateRecipe);

    // Assert
    assertEquals(1, result.getId());
    assertEquals("New Recipe1", result.getName());
    // verify findById and save methods
    verify(recipeRepository, times(1)).findById(1);
    verify(recipeRepository, times(1)).save(any(Recipe.class));
  }

  @Test
  void testCreateRecipe_Success() {
    // Arrange
    Recipe newRecipe =
        createRecipe(null, "Recipe1", "Ingredient1, Ingredient2", Category.DINNER, 30, 4);
    Recipe savedRecipe =
        createRecipe(1, "Recipe1", "Ingredient1, Ingredient2", Category.DINNER, 30, 4);
    // Mock
    when(recipeRepository.findByNameContainingIgnoreCase("Recipe1"))
        .thenReturn(Collections.emptyList());
    when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);

    // Act
    Recipe result = recipeService.createRecipe(newRecipe);

    assertEquals(1, result.getId());
    assertEquals("Recipe1", result.getName());

    verify(recipeRepository, times(1)).findByNameContainingIgnoreCase("Recipe1");
    verify(recipeRepository, times(1)).save(newRecipe);
  }

  @Test
  void testCreateRecipe_DuplicateName() {
    // Arrange
    Recipe existingRecipe =
        createRecipe(1, "Recipe1", "Ingredient1, Ingredient2", Category.DINNER, 30, 4);
    Recipe newRecipe =
        createRecipe(null, "Recipe1", "Ingredient1, Ingredient2", Category.DINNER, 30, 4);

    // Mock - name already exists (return list with existing recipe)
    when(recipeRepository.findByNameContainingIgnoreCase("Recipe1"))
        .thenReturn(Arrays.asList(existingRecipe));

    // Act & Assert
    DuplicateRecipeException exception =
        assertThrows(DuplicateRecipeException.class, () -> recipeService.createRecipe(newRecipe));

    assertTrue(exception.getMessage().contains("Recipe with name Recipe1 already exists"));

    // Verify
    verify(recipeRepository, times(1)).findByNameContainingIgnoreCase("Recipe1");
  }

  @Test
  void testCreateRecipe_NegativePrepTime() {
    // Arrange
    Recipe newRecipe =
        createRecipe(null, "Recipe1", "Ingredient1, Ingredient2", Category.DINNER, -30, 4);

    // Mock not necessary: Service throws exception before checking repository

    // Act & Assert
    InvalidPrepTimeException exception =
        assertThrows(InvalidPrepTimeException.class, () -> recipeService.createRecipe(newRecipe));

    // Check exception message (verify with your actual exception class)
    assertNotNull(exception.getMessage());

    // Verify - repository methods should NOT be called at all
    verify(recipeRepository, never()).findByNameContainingIgnoreCase(anyString());
    verify(recipeRepository, never()).save(any(Recipe.class));
  }

  @Test
  void testCreateRecipe_PrepTimeTooHigh() {
    // Arrange
    Recipe newRecipe =
        createRecipe(null, "Recipe1", "Ingredient1, Ingredient2", Category.DINNER, 10000, 4);

    // Mock not necessary: Service throws exception before checking repository

    // Act & Assert
    InvalidPrepTimeException exception =
        assertThrows(InvalidPrepTimeException.class, () -> recipeService.createRecipe(newRecipe));

    // Check exception message (verify with your actual exception class)
    assertNotNull(exception.getMessage());

    // Verify - repository methods should NOT be called at all
    verify(recipeRepository, never()).findByNameContainingIgnoreCase(anyString());
    verify(recipeRepository, never()).save(any(Recipe.class));
  }
}
