package com.learning.recipeapi.repository;

import com.learning.recipeapi.Category;
import com.learning.recipeapi.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Integer> {

  List<Recipe> findByNameContainingIgnoreCase(String text);

  List<Recipe> findByIngredientsTextContainingIgnoreCase(String ingredientsText);

  List<Recipe> findByCategory(Category category);

  List<Recipe> findByPrepTimeMinutesLessThan(Integer minutes);

  List<Recipe> findByServings(Integer servings);

  List<Recipe> findByPrepTimeMinutesBetween(Integer min, Integer max);
}
