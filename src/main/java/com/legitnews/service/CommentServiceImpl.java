package com.legitnews.service;

import com.legitnews.dto.CommentDTO;
import com.legitnews.entity.Comment;
import com.legitnews.entity.News;
import com.legitnews.entity.User;
import com.legitnews.repository.CommentRepository;
import com.legitnews.repository.NewsRepository;
import com.legitnews.repository.UserRepository;
import com.legitnews.util.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class CommentServiceImpl implements CommentService {
  private final NewsRepository newsRepo;
  private final UserRepository userRepo;
  private final CommentRepository commentRepo;
  private final Mappers mappers; // <-- inject the mapper bean

  public CommentServiceImpl(NewsRepository newsRepo,
                            UserRepository userRepo,
                            CommentRepository commentRepo,
                            Mappers mappers) {
    this.newsRepo = newsRepo;
    this.userRepo = userRepo;
    this.commentRepo = commentRepo;
    this.mappers = mappers;
  }

  @Override
  public Page<CommentDTO> listByNews(Long newsId, int page, int size) {
    News n = newsRepo.findById(newsId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found"));

    var pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by("createdAt").descending());
    var p = commentRepo.findByNews(n, pageable);

    return p.map(mappers::toDTO); // <-- instance method reference
  }

  @Override
  public CommentDTO add(Long newsId, Long userId, String content, String imageUrl, boolean anonymous) {
    News n = newsRepo.findById(newsId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found"));
    User u = userRepo.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

    Comment c = Comment.builder()
        .news(n)
        .user(u)
        .content(content)
        .imageUrl(imageUrl)
        .anonymous(anonymous)
        .createdAt(LocalDateTime.now())
        .build();

    return mappers.toDTO(commentRepo.save(c)); // <-- use injected mapper
  }
}
