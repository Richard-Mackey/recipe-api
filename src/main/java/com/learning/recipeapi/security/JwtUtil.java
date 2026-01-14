package com.learning.recipeapi.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {
  @Value("${jwt.secret}")
  private String SECRET_KEY;

  @Value("${jwt.expiration}")
  private long EXPIRATION_TIME;

  public String generateToken(String username) {
    Date issuedAt = new Date();
    Date expiresAt = new Date(System.currentTimeMillis() + EXPIRATION_TIME);

    return Jwts.builder()
        .setSubject(username)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
        .compact();
  }

  public String extractUsername(String token) {
    return Jwts.parser()
        .setSigningKey(SECRET_KEY.getBytes())
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public boolean validateToken(String token, String username) {
    final String extractedUsername = extractUsername(token);
    return (username.equals(extractedUsername) && !isTokenExpired(token));
  }

  private boolean isTokenExpired(String token) {
    Date expiration =
        Jwts.parser()
            .setSigningKey(SECRET_KEY.getBytes())
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getExpiration();
    return expiration.before(new Date());
  }
}
