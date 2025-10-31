// src/main/java/com/legitnews/entity/CommentReport.java
package com.legitnews.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name="comment_reports")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CommentReport {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long newsId;
  private Long commentId;
  private Long reporterId;          // nullable for anonymous if you allow
  private String reason;

  private LocalDateTime createdAt;
}