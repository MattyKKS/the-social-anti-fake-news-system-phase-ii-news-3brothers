// src/main/java/com/legitnews/controller/AdminCommentQueryController.java
package com.legitnews.controller;

import com.legitnews.dto.admin.DeletedCommentAdminDTO;
import com.legitnews.dto.admin.ReportedCommentAdminDTO;
import com.legitnews.entity.Comment;
import com.legitnews.entity.CommentDeleteLog;
import com.legitnews.entity.CommentReport;
import com.legitnews.entity.News;
import com.legitnews.repository.CommentDeleteLogRepository;
import com.legitnews.repository.CommentReportRepository;
import com.legitnews.repository.CommentRepository;
import com.legitnews.repository.NewsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/comments")
public class AdminCommentQueryController {

  private final CommentReportRepository commentReportRepo;
  private final CommentDeleteLogRepository deleteLogRepo;
  private final CommentRepository commentRepo;
  private final NewsRepository newsRepo;

  public AdminCommentQueryController(CommentReportRepository commentReportRepo,
                                     CommentDeleteLogRepository deleteLogRepo,
                                     CommentRepository commentRepo,
                                     NewsRepository newsRepo) {
    this.commentReportRepo = commentReportRepo;
    this.deleteLogRepo = deleteLogRepo;
    this.commentRepo = commentRepo;
    this.newsRepo = newsRepo;
  }

  // ---- GET /api/admin/comments/reported ----
  @GetMapping("/reported")
  public Page<ReportedCommentAdminDTO> listReported(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "50") int size) {

    // Load reports newest->oldest, then group by commentId
    List<CommentReport> all = commentReportRepo.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    Map<Long, List<CommentReport>> grouped = all.stream()
        .collect(Collectors.groupingBy(CommentReport::getCommentId));

    List<ReportedCommentAdminDTO> rows = new ArrayList<>();

    for (Map.Entry<Long, List<CommentReport>> entry : grouped.entrySet()) {
      Long commentId = entry.getKey();
      List<CommentReport> reports = entry.getValue();

      // Base fields
      Long newsId = reports.get(0).getNewsId();

      // ---- content/author from live comment, else fallback to delete log
      String content = "";
      String authorName = "Anonymous";

      Optional<Comment> commentOpt = commentRepo.findById(commentId);
      if (commentOpt.isPresent()) {
        Comment c = commentOpt.get();
        content = c.getContent() == null ? "" : c.getContent();
        authorName = c.isAnonymous()
            ? "Anonymous"
            : (c.getUser() != null && c.getUser().getName() != null ? c.getUser().getName() : "Anonymous");
      } else {
        // Fallback: latest delete log for this commentId (no repo method needed)
        Optional<CommentDeleteLog> latestLog = deleteLogRepo
            .findAll(Sort.by(Sort.Direction.DESC, "deletedAt"))
            .stream()
            .filter(l -> Objects.equals(l.getCommentId(), commentId))
            .findFirst();

        if (latestLog.isPresent()) {
          CommentDeleteLog log = latestLog.get();
          content = log.getContentSnapshot() == null ? "" : log.getContentSnapshot();
          authorName = log.getAuthorNameSnapshot() == null ? "Anonymous" : log.getAuthorNameSnapshot();
        }
      }

      // ---- news headline (adjust getter if your field name differs)
      String newsHeadline = newsRepo.findById(newsId)
          .map(n -> {
            try { return n.getHeadline(); } catch (Exception e) { return null; }
          })
          .orElse(null);

      // Oldest report time & most recent reason
      reports.sort(Comparator.comparing(CommentReport::getCreatedAt));
      var firstReportedAt = reports.get(0).getCreatedAt();
      var lastReason = reports.get(reports.size() - 1).getReason();

      rows.add(ReportedCommentAdminDTO.builder()
          .newsId(newsId)
          .commentId(commentId)
          .content(content)
          .authorName(authorName)
          .newsHeadline(newsHeadline)
          .firstReportedAt(firstReportedAt)
          .lastReason(lastReason == null ? "" : lastReason)
          .reports(reports.size())
          .build());
    }

    // In-memory pagination
    int from = Math.max(0, page * size);
    int to = Math.min(rows.size(), from + size);
    List<ReportedCommentAdminDTO> slice = from >= to ? List.of() : rows.subList(from, to);

    return new PageImpl<>(slice, PageRequest.of(page, size), rows.size());
  }

  // ---- GET /api/admin/comments/deleted ----
  @GetMapping("/deleted")
  public Page<DeletedCommentAdminDTO> listDeleted(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "50") int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "deletedAt"));
    Page<CommentDeleteLog> p = deleteLogRepo.findAll(pageable);

    List<DeletedCommentAdminDTO> mapped = p.getContent().stream().map(log -> {
      String newsHeadline = newsRepo.findById(log.getNewsId() == null ? -1L : log.getNewsId())
          .map(n -> {
            try { return n.getHeadline(); } catch (Exception e) { return null; }
          })
          .orElse(null);

      return DeletedCommentAdminDTO.builder()
          .newsId(log.getNewsId())
          .commentId(log.getCommentId())
          .contentSnapshot(Optional.ofNullable(log.getContentSnapshot()).orElse(""))
          .authorNameSnapshot(Optional.ofNullable(log.getAuthorNameSnapshot()).orElse("Anonymous"))
          .newsHeadline(newsHeadline)
          .deletedByAdminId(log.getDeletedByAdminId())
          .reason(Optional.ofNullable(log.getReason()).orElse(""))
          .deletedAt(log.getDeletedAt())
          .build();
    }).toList();

    return new PageImpl<>(mapped, pageable, p.getTotalElements());
  }
}
