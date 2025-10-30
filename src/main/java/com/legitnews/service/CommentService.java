package com.legitnews.service;

import com.legitnews.dto.CommentDTO;
import org.springframework.data.domain.Page;

public interface CommentService {
  Page<CommentDTO> listByNews(Long newsId, int page, int size);
  CommentDTO add(Long newsId, Long userId, String content, String imageUrl, boolean anonymous);
    CommentDTO edit(Long newsId, Long commentId, Long userId, String content, String imageUrl, boolean anonymous);
    void delete(Long newsId, Long commentId, Long userId);
    void reportComment(Long newsId, Long commentId, Long reporterId, String reason);
    void adminDeleteComment(Long commentId, Long adminId, String reason);
}
