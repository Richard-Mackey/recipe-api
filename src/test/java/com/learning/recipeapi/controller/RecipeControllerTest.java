package com.learning.recipeapi.controller;

import com.learning.recipeapi.entity.Recipe;
import com.learning.recipeapi.Category;
import com.learning.recipeapi.service.RecipeService;
import com.learning.recipeapi.exception.RecipeNotFoundException;
import com.learning.recipeapi.exception.DuplicateRecipeException;
import com.learning.recipeapi.exception.InvalidPrepTimeException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RecipeControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private RecipeService recipeService;

  @Autowired private ObjectMapper objectMapper;

  @Test
  public void testGetRecipeById_Success() throws Exception {

    // arrange - create recipe
    Recipe recipe = new Recipe();
    recipe.setId(1);
    recipe.setName("Test Recipe");
    recipe.setIngredientsText("Test ingredients");
    recipe.setInstructions("Test instructions");
    recipe.setPrepTimeMinutes(30);
    recipe.setServings(4);
    recipe.setCategory(Category.BREAKFAST);

    // Mock - tell service what to return
    when(recipeService.getRecipeById(1)).thenReturn(recipe);
    // Perform get request and verify response
    mockMvc
        .perform(get("/recipes/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Test Recipe"))
        .andExpect(jsonPath("$.ingredientsText").value("Test ingredients"))
        .andExpect(jsonPath("$.instructions").value("Test instructions"))
        .andExpect(jsonPath("$.prepTimeMinutes").value(30))
        .andExpect(jsonPath("$.servings").value(4))
        .andExpect(jsonPath("$.category").value("BREAKFAST"));

    verify(recipeService, times(1)).getRecipeById(1);
  }

  @Test
  public void testRecipeById_NotFound() throws Exception {
    // arrange
    when(recipeService.getRecipeById(1))
        .thenThrow(new RecipeNotFoundException("Recipe not found with id 1"));

    // Act and assert
    mockMvc
        .perform(get("/recipes/1"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.errorCode").value("RECIPE_NOT_FOUND"))
        .andExpect(jsonPath("$.error").value("Recipe not found with id 1"))
        .andExpect(jsonPath("$.status").value(404));

    // Verify -check the service method was indeed called
    verify(recipeService, times(1)).getRecipeById(1);
  }

  @Test
  public void testCreateRecipe_Success() throws Exception {
    // arrange - create a test to send in a request
    Recipe requestRecipe = new Recipe();
    requestRecipe.setName("Test Recipe");
    requestRecipe.setIngredientsText("Test ingredients");
    requestRecipe.setInstructions("Test instructions");
    requestRecipe.setPrepTimeMinutes(30);
    requestRecipe.setServings(4);
    requestRecipe.setCategory(Category.BREAKFAST);

    // create recipe that service will return with id assigned
    Recipe savedRecipe = new Recipe();
    savedRecipe.setId(1);
    savedRecipe.setName("Test Recipe");
    savedRecipe.setIngredientsText("Test ingredients");
    savedRecipe.setInstructions("Test instructions");
    savedRecipe.setPrepTimeMinutes(30);
    savedRecipe.setServings(4);
    savedRecipe.setCategory(Category.BREAKFAST);

    // mock- tell service what is returned
    when(recipeService.createRecipe(any(Recipe.class))).thenReturn(savedRecipe);

    // act and assert

    mockMvc
        .perform(
            post("/recipes")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestRecipe)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Test Recipe"))
        .andExpect(jsonPath("$.ingredientsText").value("Test ingredients"))
        .andExpect(jsonPath("$.instructions").value("Test instructions"))
        .andExpect(jsonPath("$.prepTimeMinutes").value(30))
        .andExpect(jsonPath("$.servings").value(4))
        .andExpect(jsonPath("$.category").value("BREAKFAST"));
    // verify
    verify(recipeService, times(1)).createRecipe(any(Recipe.class));
  }

  @Test
  public void testCreateRecipe_InvalidInput() throws Exception {
    // arrange with a missing required field
    Recipe invalidRecipe = new Recipe();
    invalidRecipe.setName("Test Recipe");
    invalidRecipe.setIngredientsText(""); // ingredients field cannot be empty
    invalidRecipe.setInstructions("Test instructions");
    invalidRecipe.setPrepTimeMinutes(30);
    invalidRecipe.setServings(4);
    invalidRecipe.setCategory(Category.BREAKFAST);

    // act and assert
    mockMvc
        .perform(
            post("/recipes")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidRecipe)))
        .andExpect(status().isBadRequest());

    // verify
    verify(recipeService, never()).createRecipe(any(Recipe.class));
  }

  @Test
  public void testCreateRecipe_DuplicateName() throws Exception {
    Recipe duplicateRecipe = new Recipe();
    duplicateRecipe.setName("Test Recipe");
    duplicateRecipe.setIngredientsText("Test ingredients");
    duplicateRecipe.setInstructions("Test instructions");
    duplicateRecipe.setPrepTimeMinutes(30);
    duplicateRecipe.setServings(4);
    duplicateRecipe.setCategory(Category.BREAKFAST);

    when(recipeService.createRecipe(any(Recipe.class)))
        .thenThrow(new DuplicateRecipeException("Test Recipe"));

    // act and assert - perform POST and verify 409 conflict
    mockMvc
        .perform(
            post("/recipes")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(duplicateRecipe)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.errorCode").value("DUPLICATE_RECIPE"))
        .andExpect(jsonPath("$.error").value("Recipe with name Test Recipe already exists"))
        .andExpect(jsonPath("$.status").value(409));

    // verify
    verify(recipeService, times(1)).createRecipe(any(Recipe.class));
  }

  @Test
  public void testUpdateRecipe_Success() throws Exception {
    // arrange - create a recipe to send in a PUT request
    Recipe updateRecipe = new Recipe();
    updateRecipe.setName("Test Recipe");
    updateRecipe.setIngredientsText("Test ingredients");
    updateRecipe.setInstructions("Test instructions");
    updateRecipe.setPrepTimeMinutes(30);
    updateRecipe.setServings(4);
    updateRecipe.setCategory(Category.BREAKFAST);

    // create a recipe that service will return with Id
    Recipe updatedRecipe = new Recipe();
    updatedRecipe.setId(1);
    updatedRecipe.setName("Test Recipe");
    updatedRecipe.setIngredientsText("Test ingredients");
    updatedRecipe.setInstructions("Test instructions");
    updatedRecipe.setPrepTimeMinutes(30);
    updatedRecipe.setServings(4);
    updatedRecipe.setCategory(Category.BREAKFAST);

    // mock - tell service what to return
    when(recipeService.updateRecipe(eq(1), any(Recipe.class))).thenReturn(updatedRecipe);

    // act and assert
    mockMvc
        .perform(
            put("/recipes/1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(updateRecipe)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Test Recipe"))
        .andExpect(jsonPath("$.ingredientsText").value("Test ingredients"))
        .andExpect(jsonPath("$.instructions").value("Test instructions"))
        .andExpect(jsonPath("$.prepTimeMinutes").value(30))
        .andExpect(jsonPath("$.servings").value(4))
        .andExpect(jsonPath("$.category").value("BREAKFAST"));

    // verify
    verify(recipeService, times(1)).updateRecipe(eq(1), any(Recipe.class));
  }

  @Test
  public void testDeleteRecipe_Success() throws Exception {
    // no arramge needed - DELETE just needs an Id
    // no mock needed - deleteRecipe() is void, so nothing to return
    // service will complete succesfully by default

    // act and assert - perform DELETE request and verify 204 No Content

    mockMvc.perform(delete("/recipes/1")).andExpect(status().isNoContent());

    // verify - check service method was called
    verify(recipeService, times(1)).deleteRecipe(eq(1));
  }

  @Test
  public void testCreateRecipe_CookingTimeTooShort() throws Exception {
    // arrange with a prep time of 0
    Recipe invalidRecipe = new Recipe();
    invalidRecipe.setName("Test Recipe");
    invalidRecipe.setIngredientsText("Delicious ingredients"); // ingredients field cannot be empty
    invalidRecipe.setInstructions("Test instructions");
    invalidRecipe.setPrepTimeMinutes(0);
    invalidRecipe.setServings(4);
    invalidRecipe.setCategory(Category.BREAKFAST);

    // act and assert
    mockMvc
        .perform(
            post("/recipes")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidRecipe)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
        .andExpect(
            jsonPath("$.fieldErrors.prepTimeMinutes")
                .value("Cooking time must be between 1 and 480 minutes"));

    // verify
    verify(recipeService, never()).createRecipe(any(Recipe.class));
  }

  @Test
  public void testCreateRecipe_CookingTimeTooLong() throws Exception {
    // arrange with a prep time of 900
    Recipe invalidRecipe = new Recipe();
    invalidRecipe.setName("Test Recipe");
    invalidRecipe.setIngredientsText("Delicious ingredients");
    invalidRecipe.setInstructions("Test instructions");
    invalidRecipe.setPrepTimeMinutes(900);
    invalidRecipe.setServings(4);
    invalidRecipe.setCategory(Category.BREAKFAST);

    // act and assert
    mockMvc
        .perform(
            post("/recipes")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidRecipe)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
        .andExpect(
            jsonPath("$.fieldErrors.prepTimeMinutes")
                .value("Cooking time must be between 1 and 480 minutes"));

    // verify
    verify(recipeService, never()).createRecipe(any(Recipe.class));
  }

  @Test
  public void testCreateRecipe_CookingTimeNull() throws Exception {
    // Arrange - request recipe WITHOUT prepTimeMinutes set
    Recipe recipe = new Recipe();
    recipe.setName("Test Recipe");
    recipe.setIngredientsText("Delicious ingredients");
    recipe.setInstructions("Test instructions");
    recipe.setPrepTimeMinutes(null);
    recipe.setServings(4);
    recipe.setCategory(Category.BREAKFAST);

    // Create saved recipe that service returns
    Recipe savedRecipe = new Recipe();
    savedRecipe.setId(1);
    savedRecipe.setName("Test Recipe");
    savedRecipe.setIngredientsText("Delicious ingredients");
    savedRecipe.setInstructions("Test instructions");
    savedRecipe.setPrepTimeMinutes(null);
    savedRecipe.setServings(4);
    savedRecipe.setCategory(Category.BREAKFAST);

    // Mock - tell service what to return
    when(recipeService.createRecipe(any(Recipe.class))).thenReturn(savedRecipe);

    // act and assert

    mockMvc
        .perform(
            post("/recipes")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(recipe)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Test Recipe"))
        .andExpect(jsonPath("$.ingredientsText").value("Delicious ingredients"))
        .andExpect(jsonPath("$.instructions").value("Test instructions"))
        .andExpect(jsonPath("$.prepTimeMinutes").doesNotExist())
        .andExpect(jsonPath("$.servings").value(4))
        .andExpect(jsonPath("$.category").value("BREAKFAST"));
    // verify
    verify(recipeService, times(1)).createRecipe(any(Recipe.class));
  }

  @Test
  public void testCreateRecipe_CookingTimeAtMinimumBoundary() throws Exception {
    // arrange - create a test to send in a request
    Recipe requestRecipe = new Recipe();
    requestRecipe.setName("Test Recipe");
    requestRecipe.setIngredientsText("Test ingredients");
    requestRecipe.setInstructions("Test instructions");
    requestRecipe.setPrepTimeMinutes(1);
    requestRecipe.setServings(4);
    requestRecipe.setCategory(Category.BREAKFAST);

    // create recipe that service will return with id assigned
    Recipe savedRecipe = new Recipe();
    savedRecipe.setId(1);
    savedRecipe.setName("Test Recipe");
    savedRecipe.setIngredientsText("Test ingredients");
    savedRecipe.setInstructions("Test instructions");
    savedRecipe.setPrepTimeMinutes(1);
    savedRecipe.setServings(4);
    savedRecipe.setCategory(Category.BREAKFAST);

    // mock- tell service what is returned
    when(recipeService.createRecipe(any(Recipe.class))).thenReturn(savedRecipe);

    // act and assert

    mockMvc
        .perform(
            post("/recipes")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestRecipe)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Test Recipe"))
        .andExpect(jsonPath("$.ingredientsText").value("Test ingredients"))
        .andExpect(jsonPath("$.instructions").value("Test instructions"))
        .andExpect(jsonPath("$.prepTimeMinutes").value(1))
        .andExpect(jsonPath("$.servings").value(4))
        .andExpect(jsonPath("$.category").value("BREAKFAST"));
    // verify
    verify(recipeService, times(1)).createRecipe(any(Recipe.class));
  }

  @Test
  public void testCreateRecipe_CookingTimeAtMaximumBoundary() throws Exception {
    // arrange - create a test to send in a request
    Recipe requestRecipe = new Recipe();
    requestRecipe.setName("Test Recipe");
    requestRecipe.setIngredientsText("Test ingredients");
    requestRecipe.setInstructions("Test instructions");
    requestRecipe.setPrepTimeMinutes(480);
    requestRecipe.setServings(4);
    requestRecipe.setCategory(Category.BREAKFAST);

    // create recipe that service will return with id assigned
    Recipe savedRecipe = new Recipe();
    savedRecipe.setId(1);
    savedRecipe.setName("Test Recipe");
    savedRecipe.setIngredientsText("Test ingredients");
    savedRecipe.setInstructions("Test instructions");
    savedRecipe.setPrepTimeMinutes(480);
    savedRecipe.setServings(4);
    savedRecipe.setCategory(Category.BREAKFAST);

    // mock- tell service what is returned
    when(recipeService.createRecipe(any(Recipe.class))).thenReturn(savedRecipe);

    // act and assert

    mockMvc
        .perform(
            post("/recipes")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(requestRecipe)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Test Recipe"))
        .andExpect(jsonPath("$.ingredientsText").value("Test ingredients"))
        .andExpect(jsonPath("$.instructions").value("Test instructions"))
        .andExpect(jsonPath("$.prepTimeMinutes").value(480))
        .andExpect(jsonPath("$.servings").value(4))
        .andExpect(jsonPath("$.category").value("BREAKFAST"));
    // verify
    verify(recipeService, times(1)).createRecipe(any(Recipe.class));
  }

  @Test
  public void testCreateRecipe_BlankName() throws Exception {
    // arrange with a blank name
    Recipe invalidRecipe = new Recipe();
    invalidRecipe.setName("");
    invalidRecipe.setIngredientsText("Delicious ingredients");
    invalidRecipe.setInstructions("Test instructions");
    invalidRecipe.setPrepTimeMinutes(60);
    invalidRecipe.setServings(4);
    invalidRecipe.setCategory(Category.BREAKFAST);

    // act and assert
    mockMvc
        .perform(
            post("/recipes")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidRecipe)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
        .andExpect(
            jsonPath("$.fieldErrors.name").value("Recipe name must be between 1-100 characters"));

    // verify
    verify(recipeService, never()).createRecipe(any(Recipe.class));
  }

  @Test
  public void testCreateRecipe_BlankInstructions() throws Exception {
    // arrange with a blank instructions
    Recipe invalidRecipe = new Recipe();
    invalidRecipe.setName("Test Recipe");
    invalidRecipe.setIngredientsText("Delicious ingredients");
    invalidRecipe.setInstructions("");
    invalidRecipe.setPrepTimeMinutes(60);
    invalidRecipe.setServings(4);
    invalidRecipe.setCategory(Category.BREAKFAST);

    // act and assert
    mockMvc
        .perform(
            post("/recipes")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidRecipe)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
        .andExpect(
            jsonPath("$.fieldErrors.instructions").value("Instructions cannot be left empty"));

    // verify
    verify(recipeService, never()).createRecipe(any(Recipe.class));
  }

  @Test
  public void testCreateRecipe_MultipleValidationErrors() throws Exception {
    // arrange with multiple invalid fields
    Recipe invalidRecipe = new Recipe();
    invalidRecipe.setName("");
    invalidRecipe.setIngredientsText("");
    invalidRecipe.setInstructions("");
    invalidRecipe.setPrepTimeMinutes(0);
    invalidRecipe.setServings(4);
    invalidRecipe.setCategory(Category.BREAKFAST);

    // act and assert
    mockMvc
        .perform(
            post("/recipes")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidRecipe)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_FAILED"))
        .andExpect(jsonPath("$.fieldErrors.name").exists())
        .andExpect(jsonPath("$.fieldErrors.ingredientsText").exists())
        .andExpect(jsonPath("$.fieldErrors.instructions").exists())
        .andExpect(jsonPath("$.fieldErrors.prepTimeMinutes").exists());
    // verify
    verify(recipeService, never()).createRecipe(any(Recipe.class));
  }
}
