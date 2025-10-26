package com.legitnews.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name="news")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class News {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank private String category;
  @NotBlank private String headline;

  @Column(length=4000)
  private String details;

  @NotBlank private String reporter;

  private LocalDateTime dateTime;   // parsed from your "date" string
  private String imageUrl;          // Firebase URL

  @Enumerated(EnumType.STRING)
  private NewsStatus status;        // REAL/FAKE/UNKNOWN

  private int votesReal;
  private int votesFake;
}
