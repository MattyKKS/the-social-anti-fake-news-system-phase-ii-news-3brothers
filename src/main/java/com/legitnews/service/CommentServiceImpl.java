package com.legitnews.service;

import com.legitnews.dto.CommentDTO;
import com.legitnews.entity.Comment;
import com.legitnews.entity.CommentDeleteLog;
import com.legitnews.entity.CommentReport;
import com.legitnews.entity.News;
import com.legitnews.entity.User;
import com.legitnews.repository.CommentRepository;
import com.legitnews.repository.NewsRepository;
import com.legitnews.repository.UserRepository;
import com.legitnews.repository.CommentReportRepository;
import com.legitnews.repository.CommentDeleteLogRepository;  
import com.legitnews.util.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import org.springframework.util.StringUtils;

@Service
public class CommentServiceImpl implements CommentService {
  private final NewsRepository newsRepo;
  private final UserRepository userRepo;
  private final CommentRepository commentRepo;
  private final CommentReportRepository commentReportRepo;
  private final CommentDeleteLogRepository commentDeleteLogRepo;
  private final Mappers mappers; // <-- inject the mapper bean

  public CommentServiceImpl(NewsRepository newsRepo,
                            UserRepository userRepo,
                            CommentRepository commentRepo,
                            CommentReportRepository commentReportRepo,
                            CommentDeleteLogRepository commentDeleteLogRepo,
                            Mappers mappers) {
    this.newsRepo = newsRepo;
    this.userRepo = userRepo;
    this.commentRepo = commentRepo;
    this.commentReportRepo = commentReportRepo;
    this.commentDeleteLogRepo = commentDeleteLogRepo;
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

    boolean hasImage = StringUtils.hasText(imageUrl);
    boolean hasText  = StringUtils.hasText(content);

    if (!hasText && !hasImage) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Content or image is required");
    }
    if (!hasText && hasImage) {
      content = "(image)"; // satisfy @NotBlank
    }        

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

  @Override
  public CommentDTO edit(Long newsId, Long commentId, Long userId, String content, String imageUrl, boolean anonymous) {
    var c = commentRepo.findById(commentId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

    if (!c.getNews().getId().equals(newsId)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment/news mismatch");
    }
    if (!c.getUser().getId().equals(userId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can edit only your comment");
    }

    boolean hasImage = StringUtils.hasText(imageUrl);
    boolean hasText  = StringUtils.hasText(content);

    if (!hasText && !hasImage) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Content or image is required");
    }
    if (!hasText && hasImage) {
      content = "(image)";
    }

    c.setContent(content);
    c.setImageUrl(imageUrl);
    c.setAnonymous(anonymous);
    var saved = commentRepo.save(c);
    return mappers.toDTO(saved);
  }

  @Override
  public void delete(Long newsId, Long commentId, Long userId) {
    var c = commentRepo.findById(commentId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

    if (!c.getNews().getId().equals(newsId)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment/news mismatch");
    }
    if (!c.getUser().getId().equals(userId)) {
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can delete only your comment");
    }

    commentRepo.delete(c);
  }

  @Override
  public void reportComment(Long newsId, Long commentId, Long reporterId, String reason) {
    News n = newsRepo.findById(newsId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found"));
    Comment c = commentRepo.findById(commentId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

    if (!c.getNews().getId().equals(n.getId())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment/news mismatch");
    }

    CommentReport rpt = CommentReport.builder()
        .newsId(newsId)
        .commentId(commentId)
        .reporterId(reporterId)                      // can be null if you allow anonymous
        .reason(reason == null ? "" : reason)
        .createdAt(LocalDateTime.now())
        .build();

    commentReportRepo.save(rpt);
  }

  @Override
  @Transactional
  public void adminDeleteComment(Long commentId, Long adminId, String reason) {
    Comment c = commentRepo.findById(commentId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found"));

    // Snapshot info for history (respect "anonymous")
    String authorName = "Anonymous";
    if (c.getUser() != null && !c.isAnonymous()) {
      authorName = c.getUser().getName();
    }

    CommentDeleteLog log = CommentDeleteLog.builder()
        .newsId(c.getNews().getId())
        .commentId(c.getId())
        .contentSnapshot(c.getContent())
        .authorNameSnapshot(authorName)
        .deletedByAdminId(adminId)
        .reason(reason == null ? "" : reason)
        .deletedAt(LocalDateTime.now())
        .build();

    commentDeleteLogRepo.save(log);

        // hard delete, no restore
    commentRepo.delete(c);
  }
}
