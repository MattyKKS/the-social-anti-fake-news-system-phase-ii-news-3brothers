// src/main/java/com/legitnews/controller/AdminCommentController.java
package com.legitnews.controller;

import com.legitnews.service.CommentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/comments")
public class AdminCommentController {
  private final CommentService commentService;
  public AdminCommentController(CommentService commentService){ this.commentService = commentService; }

  // ?reason=...  (adminId can come from JWT later; here keep simple)
  @DeleteMapping("/{commentId}")
  public void delete(@PathVariable Long commentId,
                     @RequestParam(required=false) String reason,
                     @RequestParam(required=false) Long adminId) {
    commentService.adminDeleteComment(commentId, adminId, reason);
  }
}
