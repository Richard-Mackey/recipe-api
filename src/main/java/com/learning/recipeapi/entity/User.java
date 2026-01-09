package com.learning.recipeapi.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(unique = true, nullable = false)
  private String username;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(name = "spoonacular_hash")
  private String spoonacularHash;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  // TEMPORARILY COMMENT THIS OUT until Phase 2
  // @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  // private List<Recipe> recipes = new ArrayList<>();

  public User() {}

  public User(String username, String email, String password) {
    this.username = username;
    this.email = email;
    this.password = password;
  }

  // Getters and setters
  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getSpoonacularHash() {
    return spoonacularHash;
  }

  public void setSpoonacularHash(String spoonacularHash) {
    this.spoonacularHash = spoonacularHash;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  // No setter for createdAt - it's managed by @CreationTimestamp

  // Uncomment in Phase 2:
  // public List<Recipe> getRecipes() {
  //     return recipes;
  // }
  //
  // public void setRecipes(List<Recipe> recipes) {
  //     this.recipes = recipes;
  // }
}
