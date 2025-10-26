package com.legitnews.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name="comments", indexes = {
    @Index(columnList = "news_id"),
    @Index(columnList = "user_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Comment {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional=false) @JoinColumn(name="news_id")
  private News news;

  @ManyToOne(optional=false) @JoinColumn(name="user_id")
  private User user;

  @NotBlank @Column(length=1000)
  private String content;

  private LocalDateTime createdAt;
}
