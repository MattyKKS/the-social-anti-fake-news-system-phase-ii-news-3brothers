// src/main/java/com/legitnews/controller/UserController.java
package com.legitnews.controller;

import com.legitnews.dto.UserDTO;
import com.legitnews.entity.Role;
import com.legitnews.repository.UserRepository;
import com.legitnews.util.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/api/users")
public class UserController {
  private final UserRepository userRepo;
  private final Mappers mappers;

  public UserController(UserRepository userRepo, Mappers mappers) {
    this.userRepo = userRepo;
    this.mappers = mappers;
  }

  @GetMapping("/{id}")
  public UserDTO get(@PathVariable Long id) {
    var u = userRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    return mappers.toDTO(u);
  }

  // Reader clicks “Apply for membership”
  @PostMapping("/{id}/membership/request")
  public void requestMembership(@PathVariable Long id) {
    var u = userRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    if (u.getRole() == Role.MEMBER || u.getRole() == Role.ADMIN) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already a member/admin");
    }
    u.setMembershipRequested(true);
    userRepo.save(u);
  }
}
