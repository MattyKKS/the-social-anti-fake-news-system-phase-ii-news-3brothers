package com.legitnews.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name="users", uniqueConstraints = { @UniqueConstraint(columnNames = "email") })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank private String name;
  private String surname;

  @Email @NotBlank
  private String email;

  // keep for later (hash later when we add security); for now nullable to avoid blockers
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  private Role role;

  private boolean membershipRequested = false;

  private LocalDateTime createdAt;

  private String photoUrl;
}
