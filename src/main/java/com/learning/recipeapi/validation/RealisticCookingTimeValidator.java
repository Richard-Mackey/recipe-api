package com.learning.recipeapi.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RealisticCookingTimeValidator
    implements ConstraintValidator<RealisticCookingTime, Integer> {

  @Override
  public boolean isValid(Integer value, ConstraintValidatorContext context) {

    // If value is null, let @NotNull handle it (if present)
    if (value == null) {
      return true;
    }

    return value >= 1 && value <= 480;
  }
}
