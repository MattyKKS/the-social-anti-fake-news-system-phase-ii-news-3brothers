// src/main/java/com/legitnews/entity/CommentDeleteLog.java
package com.legitnews.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name="comment_delete_log")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CommentDeleteLog {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long newsId;
  private Long commentId;

  @Column(length=2000)
  private String contentSnapshot;   // text at deletion time

  private String authorNameSnapshot;
  private Long deletedByAdminId;
  private String reason;
  private LocalDateTime deletedAt;
}
