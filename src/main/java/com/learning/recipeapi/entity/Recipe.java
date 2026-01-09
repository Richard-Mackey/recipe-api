package com.learning.recipeapi.entity;

import com.learning.recipeapi.Category;
import com.learning.recipeapi.validation.RealisticCookingTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipe")
public class Recipe {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotBlank(message = "Recipe name cannot be left empty")
  @Size(min = 1, max = 100, message = "Recipe name must be between 1-100 characters")
  @Column(nullable = false)
  private String name;

  private String description;

  @NotBlank(message = "Ingredients text cannot be left empty")
  @Size(min = 1, max = 1000, message = "Ingredients must be between 1-1000 characters")
  @Column(name = "ingredients_text", columnDefinition = "TEXT")
  private String ingredientsText;

  @NotBlank(message = "Instructions cannot be left empty")
  @Size(min = 1, max = 5000, message = "Instructions must be between 1-5000 characters")
  @Column(columnDefinition = "TEXT")
  private String instructions;

  // custom annotation, used to validate recipe time (in validation package)
  @RealisticCookingTime
  private Integer prepTimeMinutes;

  private Integer servings;

  @Enumerated(EnumType.STRING)
  private Category category;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  // many recipes have many ingredients
  // saving recipe saves new ingredients
  // no REMOVE - deleting recipe doesn't delete ingredients (they are used in other recipes)
  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "recipe_ingredients", // name of join table
      joinColumns = @JoinColumn(name = "recipe_id"), // column for THIS entity (recipe)
      inverseJoinColumns =
          @JoinColumn(name = "ingredient_id") // column for OTHER entity (ingredient)
      )
  private List<Ingredient> ingredients = new ArrayList<>();

  public Recipe() {}

  public Recipe(
      String name,
      String description,
      String ingredientsText,
      String instructions,
      Integer prepTimeMinutes,
      Integer servings,
      Category category,
  User user) {
    this.name = name;
    this.description = description;
    this.ingredientsText = ingredientsText; // UPDATED
    this.instructions = instructions;
    this.prepTimeMinutes = prepTimeMinutes;
    this.servings = servings;
    this.category = category;
  }

  // Getters and Setters
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getIngredientsText() {
    return ingredientsText;
  }

  public void setIngredientsText(String ingredientsText) {
    this.ingredientsText = ingredientsText;
  }

  public String getInstructions() {
    return instructions;
  }

  public void setInstructions(String instructions) {
    this.instructions = instructions;
  }

  public Integer getPrepTimeMinutes() {
    return prepTimeMinutes;
  }

  public void setPrepTimeMinutes(Integer prepTimeMinutes) {
    this.prepTimeMinutes = prepTimeMinutes;
  }

  public Integer getServings() {
    return servings;
  }

  public void setServings(Integer servings) {
    this.servings = servings;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public User getUser() {
    return user;
  }
  public void setUser(User user) {
    this.user = user;
  }

  // Relationship getters/setters
  public List<Ingredient> getIngredients() {
    return ingredients;
  }

  public void setIngredients(List<Ingredient> ingredients) {
    this.ingredients = ingredients;
  }

  // Helper methods for managing the relationship
  public void addIngredient(Ingredient ingredient) {
    ingredients.add(ingredient);
    ingredient.getRecipes().add(this);
  }

  public void removeIngredient(Ingredient ingredient) {
    ingredients.remove(ingredient);
    ingredient.getRecipes().remove(this);
  }
}
