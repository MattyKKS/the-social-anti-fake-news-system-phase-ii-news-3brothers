package com.legitnews.dto.auth;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RegisterRequest {
  private String name;
  private String surname;
  private String email;
  private String password;
  private String photoUrl;
}
