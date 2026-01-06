package com.learning.recipeapi.repository;

import com.learning.recipeapi.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Integer> {
  // JPA provides: save(), findById(), findAll(), delete(), etc.
}
