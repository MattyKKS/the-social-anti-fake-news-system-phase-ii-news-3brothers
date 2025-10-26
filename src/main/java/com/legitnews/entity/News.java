package com.legitnews.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

  private LocalDateTime dateTime;     // parsed from "date"
  private String imageUrl;            // Firebase/public URL

  @Enumerated(EnumType.STRING)
  private NewsStatus status;

  private int votesReal;
  private int votesFake;

  @OneToMany(mappedBy="news", cascade=CascadeType.ALL, orphanRemoval=true)
  private List<Comment> comments = new ArrayList<>();
}
