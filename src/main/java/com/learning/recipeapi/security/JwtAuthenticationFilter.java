package com.learning.recipeapi.security;

import java.io.IOException;
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

  @Autowired
  public JwtAuthenticationFilter(JwtUtil jwtUtil) {
    this.jwtUtil = jwtUtil;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    // 1. Get Authorization header
    String authHeader = request.getHeader("Authorization");

    // 2. Check if header exists and starts with "Bearer "
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return; // No token, let it through (Security config will block if needed)
    }

    // 3. Extract token (remove "Bearer " prefix)
    String token = authHeader.substring(7);

    // 4. Extract username from token
    String username = jwtUtil.extractUsername(token);

    // 5. If token is valid and user not already authenticated
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      if (jwtUtil.validateToken(token, username)) {
        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }

    // 7. Continue filter chain
    filterChain.doFilter(request, response);
  }
}
