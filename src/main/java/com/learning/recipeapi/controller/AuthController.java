package com.learning.recipeapi.controller;


import com.learning.recipeapi.dto.AuthResponse;
import com.learning.recipeapi.dto.LoginRequest;
import com.learning.recipeapi.dto.RegisterRequest;
import com.learning.recipeapi.service.UserService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")

public class AuthController {
    private final UserService userService;
    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("/register")
    public AuthResponse register (@Valid @RequestBody RegisterRequest request)
    {
        return userService.registerUser(request);
    }
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request)
    {
        return userService.loginUser(request);
    }
    @GetMapping("/test")
    public String testProtected() {
        return "If you see this, your JWT is valid!";
    }
}
