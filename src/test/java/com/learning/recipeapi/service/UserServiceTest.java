package com.learning.recipeapi.service;

import com.learning.recipeapi.entity.Recipe;
import com.learning.recipeapi.entity.User;
import com.learning.recipeapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserService userService;

  private User createUser(Integer id, String username, String email, String password) {
    User user = new User();
    user.setId(id);
    user.setUsername(username);
    user.setEmail(email);
    user.setPassword(password);
    return user;
  }

  @Test
  void testCreateUser_Success() {
    User newUser = createUser(null, "Richard", "richard@richard.com", "password");
    User savedUser = createUser(1, "Richard", "richard@richard.com", "password");

    when(userRepository.existsByUsername("Richard")).thenReturn(false);
    when(userRepository.existsByEmail("richard@richard.com")).thenReturn(false);

    when(userRepository.save(any(User.class))).thenReturn(savedUser);

    User result = userService.createUser(newUser);

    assertEquals(1, result.getId());
    assertEquals("Richard", result.getUsername());

    verify(userRepository, times(1)).existsByUsername("Richard");
    verify(userRepository, times(1)).existsByEmail("richard@richard.com");
    verify(userRepository, times(1)).save(newUser);
  }

  @Test
  void testCreateUser_DuplicateUsername() {
    User existingUser = createUser(1, "Richard", "richard@richard.com", "password");
    User duplicateUser = createUser(1, "Richard", "dave@dave.com", "password1");

    when(userRepository.existsByUsername("Richard")).thenReturn(true);

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(duplicateUser));

    verify(userRepository, times(1)).existsByUsername("Richard");
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void testCreateUser_DuplicateEmail() {
    User existingEmail = createUser(1, "Richard", "richard@richard.com", "password");
    User duplicateEmail = createUser(1, "Dave", "richard@richard.com", "password1");

    when(userRepository.existsByUsername("Dave")).thenReturn(false);
    when(userRepository.existsByEmail("richard@richard.com")).thenReturn(true);

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(duplicateEmail));

    verify(userRepository, times(1)).existsByUsername("Dave");

    verify(userRepository, times(1)).existsByEmail("richard@richard.com");
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void testFindByUsername() {
    User mockUser = createUser(1, "Richard", "richard@richard.com", "password");

    when(userRepository.findByUsername("Richard")).thenReturn(Optional.of(mockUser));

    Optional<User> result = userService.findByUsername("Richard");

    assertEquals(true, result.isPresent());
    assertEquals("Richard", result.get().getUsername());

    verify(userRepository, times(1)).findByUsername("Richard");
  }
}
