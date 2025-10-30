package com.legitnews.util;

import com.legitnews.dto.CommentDTO;
import com.legitnews.dto.NewsDTO;
import com.legitnews.dto.UserDTO;
import com.legitnews.entity.Comment;
import com.legitnews.entity.News;
import com.legitnews.entity.User;
import org.springframework.stereotype.Component;

@Component
public class Mappers {

  private final ImageUrlResolver imageUrlResolver;

  public Mappers(ImageUrlResolver imageUrlResolver) {
    this.imageUrlResolver = imageUrlResolver;
  }

  public NewsDTO toDTO(News n) {
    if (n == null) return null;
    long count = (n.getComments() == null) ? 0 : n.getComments().size();

    return NewsDTO.builder()
        .id(n.getId())
        .category(n.getCategory())
        .headline(n.getHeadline())
        .details(n.getDetails())
        .reporter(n.getReporter())
        .dateTime(n.getDateTime())
        .imageUrl(n.getImageUrl())                                  // raw from SQL (unchanged)
        .imagePublicUrl(imageUrlResolver.toFirebaseUrl(n.getImageUrl())) // computed Firebase URL
        .status(n.getStatus())
        .votesReal(n.getVotesReal())
        .votesFake(n.getVotesFake())
        .commentCount(count)
        .build();
  }

  public CommentDTO toDTO(Comment c) {
    if (c == null) return null;
    String display = c.isAnonymous() ? "Anonymous" : c.getUser().getName();
    return CommentDTO.builder()
        .id(c.getId())
        .newsId(c.getNews().getId())
        .userId(c.getUser().getId())
        .userName(c.getUser().getName())
        .content(c.getContent())
        .imageUrl(imageUrlResolver.toFirebaseUrl(c.getImageUrl()))
        .createdAt(c.getCreatedAt())
        .build();
  }

  public UserDTO toDTO(User u) {
    if (u == null) return null;
    return UserDTO.builder()
        .id(u.getId())
        .name(u.getName())
        .email(u.getEmail())
        .role(u.getRole())
        .createdAt(u.getCreatedAt())
        .membershipRequested(u.isMembershipRequested())
        .build();
  }
}
