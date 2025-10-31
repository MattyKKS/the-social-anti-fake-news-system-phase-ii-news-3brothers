package com.legitnews.controller;

import com.legitnews.dto.auth.AuthResponse;
import com.legitnews.dto.auth.LoginRequest;
import com.legitnews.dto.auth.RegisterRequest;
import com.legitnews.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  private final AuthService authService;
  public AuthController(AuthService authService){ this.authService = authService; }

  @PostMapping("/register")
  public AuthResponse register(@RequestBody RegisterRequest req) { return authService.register(req); }

  @PostMapping("/login")
  public AuthResponse login(@RequestBody LoginRequest req) { return authService.login(req); }
}
