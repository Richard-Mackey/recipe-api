package com.learning.recipeapi.repository;

import com.learning.recipeapi.Category;
import com.learning.recipeapi.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

  @Query("SELECT r FROM Recipe r WHERE r.source = 'USER_CREATED' OR (r.source = 'SPOONACULAR' AND r.user.id = :userId)")
  Page<Recipe> findPublicAndUserSpoonacularRecipes(@Param("userId") Integer userId, Pageable pageable);
}
