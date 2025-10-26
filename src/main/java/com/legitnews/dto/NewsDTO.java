package com.legitnews.dto;

import com.legitnews.entity.NewsStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder
public class NewsDTO {
  Long id;
  String category;
  String headline;
  String details;
  String reporter;
  LocalDateTime dateTime;
  String imageUrl;
  NewsStatus status;
  int votesReal;
  int votesFake;
  long commentCount;
}
