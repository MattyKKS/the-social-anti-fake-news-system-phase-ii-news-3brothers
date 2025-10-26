package com.legitnews.util;

import com.legitnews.dto.*;
import com.legitnews.entity.*;

public class Mappers {
  public static NewsDTO toDTO(News n) {
    if (n == null) return null;
    long count = n.getComments() == null ? 0 : n.getComments().size();
    return NewsDTO.builder()
        .id(n.getId()).category(n.getCategory()).headline(n.getHeadline())
        .details(n.getDetails()).reporter(n.getReporter())
        .dateTime(n.getDateTime()).imageUrl(n.getImageUrl())
        .status(n.getStatus()).votesReal(n.getVotesReal()).votesFake(n.getVotesFake())
        .commentCount(count)
        .build();
  }

  public static CommentDTO toDTO(Comment c) {
    if (c == null) return null;
    return CommentDTO.builder()
        .id(c.getId())
        .newsId(c.getNews().getId())
        .userId(c.getUser().getId())
        .userName(c.getUser().getName())
        .content(c.getContent())
        .createdAt(c.getCreatedAt())
        .build();
  }

  public static UserDTO toDTO(User u) {
    if (u == null) return null;
    return UserDTO.builder()
        .id(u.getId()).name(u.getName()).email(u.getEmail()).role(u.getRole())
        .createdAt(u.getCreatedAt())
        .build();
  }
}
