// src/main/java/com/legitnews/dto/admin/ReportedCommentAdminDTO.java
package com.legitnews.dto.admin;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ReportedCommentAdminDTO {
  private Long newsId;
  private Long commentId;
  private String content;        // current comment text
  private String authorName;     // "Anonymous" if anonymous
  private String newsHeadline;   // optional (can be null/empty)
  private LocalDateTime firstReportedAt;
  private String lastReason;
  private long reports;          // number of reports
}
