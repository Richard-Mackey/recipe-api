package com.learning.recipeapi.security;

import java.io.IOException;

import com.learning.recipeapi.entity.User;
import com.learning.recipeapi.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;

  @Autowired
  public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
    this.jwtUtil = jwtUtil;
    this.userRepository = userRepository;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    System.out.println("=== JWT FILTER DEBUG ===");
    System.out.println("Request URI: " + request.getRequestURI());

    // 1. Get Authorization header
    String authHeader = request.getHeader("Authorization");
    System.out.println("Auth Header: " + authHeader);

    // 2. Check if header exists and starts with "Bearer "
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      System.out.println("No valid Bearer token found - skipping filter");
      filterChain.doFilter(request, response);
      return;
    }

    // 3. Extract token (remove "Bearer " prefix)
    String token = authHeader.substring(7);
    System.out.println(
        "Token extracted (first 20 chars): "
            + token.substring(0, Math.min(20, token.length()))
            + "...");

    try {
      // 4. Extract username from token
      String username = jwtUtil.extractUsername(token);
      System.out.println("Username extracted: " + username);

      // 5. If token is valid and user not already authenticated
      if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        System.out.println("Validating token...");
        if (jwtUtil.validateToken(token, username)) {
          System.out.println("Token valid! Loading user...");

          // Load the actual User entity
          User user =
              userRepository
                  .findByUsername(username)
                  .orElseThrow(() -> new RuntimeException("User not found: " + username));

          System.out.println("User loaded: " + user.getUsername());

          // Set the User object as the principal (not just the username string!)
          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
          //

          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);

          System.out.println("Authentication set successfully!");
          System.out.println("Authorities: " + authToken.getAuthorities());
          System.out.println("Principal type: " + authToken.getPrincipal().getClass().getName());
        } else {
          System.out.println("Token validation FAILED!");
        }
      }
    } catch (Exception e) {
      System.out.println("ERROR in JWT filter: " + e.getMessage());
      e.printStackTrace();
    }

    System.out.println("=== END JWT FILTER DEBUG ===");

    // 7. Continue filter chain
    filterChain.doFilter(request, response);

    System.out.println("=== RETURNED FROM FILTER CHAIN ===");
    System.out.println("Response status: " + response.getStatus());
  }
}
