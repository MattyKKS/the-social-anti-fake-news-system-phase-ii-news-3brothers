package com.legitnews.service;

import com.legitnews.dto.CommentDTO;
import org.springframework.data.domain.Page;

public interface CommentService {
  Page<CommentDTO> listByNews(Long newsId, int page, int size);
  CommentDTO add(Long newsId, Long userId, String content);
}
