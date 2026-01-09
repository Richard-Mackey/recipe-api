package com.learning.recipeapi.repository;

import com.learning.recipeapi.Category;
import com.learning.recipeapi.entity.Recipe;
import com.learning.recipeapi.entity.User;
import com.learning.recipeapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
public class UserRepositoryTest {
  @Autowired UserRepository userRepository;

  private User createUser(String userName, String email, String password) {
    User user = new User();
    user.setUsername(userName);
    user.setEmail(email);
    user.setPassword(password);
    return user;
  }

  @Test
  void testSaveUser() {
    User user = this.createUser("Richard", "richard@richard.com", "password");

    User savedUser = this.userRepository.save(user);

    assertNotNull(savedUser.getId());
    assertEquals("Richard", savedUser.getUsername());
    assertEquals("richard@richard.com", savedUser.getEmail());
    assertEquals("password", savedUser.getPassword());
    assertNotNull(savedUser.getCreatedAt());
  }

  @Test
  void testFindByUsername() {
    userRepository.save(createUser("Richard", "richard@richard.com", "password"));

    userRepository.save(createUser("Dave", "dave@dave.com", "password1"));

    userRepository.save(createUser("Rachel", "rachel@rachel.com", "password2"));

    Optional<User> found = userRepository.findByUsername("Richard");

    assertTrue(found.isPresent());
    assertEquals("Richard", found.get().getUsername());
  }

  @Test
  void testExistsByUsername() {
    userRepository.save(createUser("Richard", "richard@richard.com", "password"));

    assertTrue(userRepository.existsByUsername("Richard"));
    assertFalse(userRepository.existsByUsername("NonExistent"));
  }

  @Test
  void testExistsByEmail() {
    userRepository.save(createUser("Richard", "richard@richard.com", "password"));

    assertTrue(userRepository.existsByEmail("richard@richard.com"));
    assertFalse(userRepository.existsByEmail("NonExistent"));
  }

  @Test
  void testUniqueUsernameConstraint() {
    userRepository.save(createUser("Richard", "richard@richard.com", "password"));

    assertThrows(
        DataIntegrityViolationException.class,
        () -> {
          userRepository.saveAndFlush(createUser("Richard", "richard@wrongemail.com", "password"));
        });
  }

  @Test
  void testUniqueEmailConstraint() {
    userRepository.save(createUser("Richard", "richard@richard.com", "password"));

    assertThrows(
        DataIntegrityViolationException.class,
        () -> {
          userRepository.saveAndFlush(createUser("Dave", "richard@richard.com", "password"));
        });
  }
}
