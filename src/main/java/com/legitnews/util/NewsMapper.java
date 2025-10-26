package com.legitnews.util;

import com.legitnews.dto.NewsDTO;
import com.legitnews.entity.News;

public class NewsMapper {
  public static NewsDTO toDTO(News n) {
    if(n==null) return null;
    return NewsDTO.builder()
        .id(n.getId()).category(n.getCategory()).headline(n.getHeadline())
        .details(n.getDetails()).reporter(n.getReporter())
        .dateTime(n.getDateTime()).imageUrl(n.getImageUrl())
        .status(n.getStatus()).votesReal(n.getVotesReal()).votesFake(n.getVotesFake())
        .build();
  }
}
