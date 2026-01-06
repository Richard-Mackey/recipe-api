package com.learning.recipeapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD) // Can be applied to fields
@Retention(RetentionPolicy.RUNTIME) // Available at runtime
@Constraint(
    validatedBy =
        RealisticCookingTimeValidator
            .class) // Links to validator class - which does the actual checking
public @interface RealisticCookingTime {

  String message() default "Cooking time must be between 1 and 480 minutes"; // Error message

  Class<?>[] groups() default {}; // Required boilerplate

  Class<? extends Payload>[] payload() default {}; // Required boilerplate
}
