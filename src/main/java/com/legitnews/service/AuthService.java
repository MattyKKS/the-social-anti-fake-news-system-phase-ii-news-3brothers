package com.legitnews.service;

import com.legitnews.dto.auth.AuthResponse;
import com.legitnews.dto.auth.LoginRequest;
import com.legitnews.dto.auth.RegisterRequest;

public interface AuthService {
  AuthResponse register(RegisterRequest req);
  AuthResponse login(LoginRequest req);
}
