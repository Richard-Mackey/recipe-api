package com.learning.recipeapi.service;

import com.learning.recipeapi.dto.AuthResponse;
import com.learning.recipeapi.dto.LoginRequest;
import com.learning.recipeapi.dto.RegisterRequest;
import com.learning.recipeapi.entity.Recipe;
import com.learning.recipeapi.entity.User;
import com.learning.recipeapi.exception.DuplicateRecipeException;
import com.learning.recipeapi.exception.InvalidPrepTimeException;
import com.learning.recipeapi.exception.RecipeNotFoundException;
import com.learning.recipeapi.repository.RecipeRepository;
import com.learning.recipeapi.repository.UserRepository;
import com.learning.recipeapi.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final SpoonacularService spoonacularService;

  private static final Logger logger = LoggerFactory.getLogger(UserService.class);

  @Autowired
  public UserService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      JwtUtil jwtUtil,
      SpoonacularService spoonacularService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
    this.spoonacularService = spoonacularService;
  }

  public User createUser(User user) {
    logger.info("Creating user: {}", user.getUsername());

    if (userRepository.existsByUsername(user.getUsername())) {
      logger.warn("Attempted to create duplicate user: {}", user.getUsername());
      throw new IllegalArgumentException();
    }
    if (userRepository.existsByEmail(user.getEmail())) {
      logger.warn("Email already exists: {}", user.getUsername());
      throw new IllegalArgumentException();
    }
    User savedUser = userRepository.save(user);
    logger.info("Created user with id: {}", savedUser.getId());
    return savedUser;
  }

  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  public AuthResponse registerUser(RegisterRequest request) {

    // 1. Check username exists
    if (userRepository.existsByUsername(request.username())) {
      throw new IllegalArgumentException("Username already exists");
    }

    // 2. Check email exists
    if (userRepository.existsByEmail(request.email())) {
      throw new IllegalArgumentException("Email already exists");
    }

    // 3. Hash password first
    String hashedPassword = passwordEncoder.encode(request.password());

    // 4. Create new user with hashed password
    User newUser = new User();
    newUser.setUsername(request.username());
    newUser.setEmail(request.email());
    newUser.setPassword(hashedPassword);

    User savedUser = userRepository.save(newUser);

    // Call Spoonacular and store the hash
    String spoonacularHash =
        spoonacularService.connectUser(savedUser.getUsername(), savedUser.getEmail());
    savedUser.setSpoonacularHash(spoonacularHash);
    userRepository.save(savedUser); // Save again with the hash

    String token = jwtUtil.generateToken(savedUser.getUsername());
    return new AuthResponse(savedUser.getUsername(), "User registered successfully", token);
  }

  public AuthResponse loginUser(LoginRequest request) {
    User user =
        userRepository
            .findByUsername(request.username())
            .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
    if (!passwordEncoder.matches(request.password(), user.getPassword())) {
      throw new IllegalArgumentException("Invalid username or password");
    }
    String token = jwtUtil.generateToken(user.getUsername());
    return new AuthResponse(user.getUsername(), "Login successful", token);
  }
}
