package com.legitnews.service;

import com.legitnews.dto.auth.AuthResponse;
import com.legitnews.dto.auth.LoginRequest;
import com.legitnews.dto.auth.RegisterRequest;
import com.legitnews.entity.Role;
import com.legitnews.entity.User;
import com.legitnews.repository.UserRepository;
import com.legitnews.util.PasswordUtil;
import com.legitnews.util.TokenUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepo;
  @Value("${app.auth.jwt.secret}")     private String secret;
  @Value("${app.auth.jwt.expires-min}") private long expMin;

  public AuthServiceImpl(UserRepository userRepo) { this.userRepo = userRepo; }

  @Override
  public AuthResponse register(RegisterRequest req) {
    if (req.getEmail()==null || req.getEmail().isBlank() || req.getPassword()==null || req.getPassword().isBlank())
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email and password required");

    userRepo.findByEmail(req.getEmail().trim().toLowerCase()).ifPresent(u -> {
      throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
    });

    User u = User.builder()
        .name(req.getName()==null? "Anonymous" : req.getName().trim())
        .email(req.getEmail().trim().toLowerCase())
        .passwordHash(PasswordUtil.hash(req.getPassword()))
        .photoUrl(req.getPhotoUrl())
        .role(Role.READER) // default on register
        .createdAt(LocalDateTime.now())
        .build();

    u = userRepo.save(u);

    String token = TokenUtil.issue(secret, u.getId(), u.getEmail(), u.getRole().name(), expMin);
    return AuthResponse.builder()
        .token(token).id(u.getId()).name(u.getName()).email(u.getEmail()).role(u.getRole())
        .build();
  }

  @Override
  public AuthResponse login(LoginRequest req) {
    User u = userRepo.findByEmail(req.getEmail()==null? "" : req.getEmail().trim().toLowerCase())
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

    if (!PasswordUtil.verify(req.getPassword()==null? "" : req.getPassword(), u.getPasswordHash()))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");

    String token = TokenUtil.issue(secret, u.getId(), u.getEmail(), u.getRole().name(), expMin);
    return AuthResponse.builder()
        .token(token).id(u.getId()).name(u.getName()).email(u.getEmail()).role(u.getRole())
        .build();
  }
}
