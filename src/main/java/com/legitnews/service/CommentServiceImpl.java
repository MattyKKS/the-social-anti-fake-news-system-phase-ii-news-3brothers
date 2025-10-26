package com.legitnews.service;

import com.legitnews.dto.CommentDTO;
import com.legitnews.entity.*;
import com.legitnews.repository.*;
import com.legitnews.util.Mappers;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CommentServiceImpl implements CommentService {
  private final NewsRepository newsRepo;
  private final UserRepository userRepo;
  private final CommentRepository commentRepo;

  public CommentServiceImpl(NewsRepository newsRepo, UserRepository userRepo, CommentRepository commentRepo) {
    this.newsRepo = newsRepo; this.userRepo = userRepo; this.commentRepo = commentRepo;
  }

  @Override
  public Page<CommentDTO> listByNews(Long newsId, int page, int size) {
    News n = newsRepo.findById(newsId).orElseThrow(() -> new RuntimeException("News not found"));
    var pageable = PageRequest.of(Math.max(0,page), Math.min(100,size), Sort.by("createdAt").descending());
    var p = commentRepo.findByNews(n, pageable);
    return p.map(Mappers::toDTO);
  }

  @Override
  public CommentDTO add(Long newsId, Long userId, String content) {
    News n = newsRepo.findById(newsId).orElseThrow(() -> new RuntimeException("News not found"));
    User u = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

    Comment c = Comment.builder()
        .news(n).user(u).content(content).createdAt(LocalDateTime.now())
        .build();
    return Mappers.toDTO(commentRepo.save(c));
  }
}
