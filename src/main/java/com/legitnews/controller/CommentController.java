package com.legitnews.controller;

import com.legitnews.dto.CommentDTO;
import com.legitnews.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/news/{newsId}/comments")
public class CommentController {
  private final CommentService service;
  public CommentController(CommentService service){ this.service = service; }

  @GetMapping
  public Page<CommentDTO> list(@PathVariable Long newsId,
                               @RequestParam(defaultValue="0") int page,
                               @RequestParam(defaultValue="10") int size) {
    return service.listByNews(newsId, page, size);
  }

  // TEMP open endpoint; when JWT is added, restrict by role
  @PostMapping
  public CommentDTO add(@PathVariable Long newsId,
                        @RequestParam Long userId,
                        @RequestParam String content,
                        @RequestParam(required = false) String imageUrl,
                        @RequestParam(defaultValue = "false") boolean anonymous) {
    return service.add(newsId, userId, content, imageUrl, anonymous);
  }
}
