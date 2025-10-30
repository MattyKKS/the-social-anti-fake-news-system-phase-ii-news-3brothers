// src/main/java/com/legitnews/controller/AdminUserController.java
package com.legitnews.controller;

import com.legitnews.dto.UserDTO;
import com.legitnews.entity.Role;
import com.legitnews.repository.UserRepository;
import com.legitnews.util.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminUserController {
  private final UserRepository userRepo;
  private final Mappers mappers;

  public AdminUserController(UserRepository userRepo, Mappers mappers) {
    this.userRepo = userRepo;
    this.mappers = mappers;
  }

  // For AdminManageUser "All Users" table (simple version)
  @GetMapping("/users")
  public List<UserDTO> allUsers() {
    return userRepo.findAll().stream().map(mappers::toDTO).toList();
  }

  @GetMapping("/membership-requests")
  public List<UserDTO> listRequests() {
    return userRepo.findAllMembershipRequests().stream().map(mappers::toDTO).toList();
  }

  @PostMapping("/membership-requests/{userId}/approve")
  public void approve(@PathVariable Long userId) {
    var u = userRepo.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    u.setRole(Role.MEMBER);
    u.setMembershipRequested(false);
    userRepo.save(u);
  }

  @PostMapping("/membership-requests/{userId}/reject")
  public void reject(@PathVariable Long userId) {
    var u = userRepo.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    u.setMembershipRequested(false);
    userRepo.save(u);
  }
}
