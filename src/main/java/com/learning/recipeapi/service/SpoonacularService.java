package com.learning.recipeapi.service;

import com.learning.recipeapi.dto.*;

import com.learning.recipeapi.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SpoonacularService {
  @Value("${spoonacular.api-key}")
  private String apiKey;

  @Value("${spoonacular.base-url}")
  private String baseUrl;

  private final RestTemplate restTemplate;

  public SpoonacularService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public String connectUser(String username, String email) {
    // 1. Build the URL
    String url = baseUrl + "/users/connect";

    // 2. Create headers with API key
    HttpHeaders headers = new HttpHeaders();
    headers.set("x-api-key", apiKey);
    headers.setContentType(MediaType.APPLICATION_JSON);

    // 3. Create request body
    Map<String, String> requestBody = new HashMap<>();
    requestBody.put("username", username);
    requestBody.put("email", email);
    requestBody.put("firstName", "User");
    requestBody.put("lastName", "User");

    // 4. Create HTTP entity (combines headers + body)
    HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

    // 5. Make POST request
    SpoonacularConnectResponse response =
        restTemplate.postForObject(url, request, SpoonacularConnectResponse.class);

    // 6. Return the hash
    return response.hash();
  }

  public SpoonacularSearchResponse searchRecipes(String query, Integer number) {
    String url =
        UriComponentsBuilder.fromHttpUrl(baseUrl + "/recipes/complexSearch")
            .queryParam("apiKey", apiKey)
            .queryParam("query", query)
            .queryParam("number", number)
            .toUriString();

    return restTemplate.getForObject(url, SpoonacularSearchResponse.class);
  }

  public SpoonacularRecipeDetailDTO getRecipeInformation(Integer spoonacularId) {
    String url =
        UriComponentsBuilder.fromHttpUrl(baseUrl + "/recipes/" + spoonacularId + "/information")
            .queryParam("apiKey", apiKey)
            .toUriString();

    return restTemplate.getForObject(url, SpoonacularRecipeDetailDTO.class);
  }
}
