package com.learning.recipeapi.config;

import com.learning.recipeapi.Category;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// This class ensures that the categories can be uppercase or lowercase - BREAKFAST or breakfast
@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addConverter(new StringToCategoryConverter());
  }

  private static class StringToCategoryConverter implements Converter<String, Category> {
    @Override
    public Category convert(String source) {
      return Category.valueOf(source.toUpperCase());
    }
  }
}
