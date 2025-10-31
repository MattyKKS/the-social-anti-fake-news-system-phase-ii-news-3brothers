package com.legitnews.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder
public class CommentDTO {
  Long id;
  Long newsId;
  Long userId;
  String userName;
  String content;
  String imageUrl;
  LocalDateTime createdAt;
}
