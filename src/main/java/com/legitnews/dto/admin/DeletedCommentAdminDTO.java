// src/main/java/com/legitnews/dto/admin/DeletedCommentAdminDTO.java
package com.legitnews.dto.admin;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DeletedCommentAdminDTO {
  private Long newsId;
  private Long commentId;
  private String contentSnapshot;
  private String authorNameSnapshot;
  private String newsHeadline;       // optional (can be null/empty)
  private Long deletedByAdminId;
  private String reason;
  private LocalDateTime deletedAt;
}
