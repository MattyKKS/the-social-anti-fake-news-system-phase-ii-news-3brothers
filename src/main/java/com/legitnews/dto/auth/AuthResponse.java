package com.legitnews.dto.auth;

import com.legitnews.entity.Role;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuthResponse {
  private String token;
  private Long id;
  private String name;
  private String email;
  private Role role;
}
