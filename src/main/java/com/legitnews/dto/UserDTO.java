package com.legitnews.dto;

import com.legitnews.entity.Role;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder
public class UserDTO {
  Long id;
  String name;
  String email;
  Role role;
  LocalDateTime createdAt;
}
