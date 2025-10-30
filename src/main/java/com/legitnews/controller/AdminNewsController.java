// src/main/java/com/legitnews/controller/AdminNewsController.java
package com.legitnews.controller;

import com.legitnews.entity.News;
import com.legitnews.dto.AdminNewsDTO;
import com.legitnews.repository.NewsRepository;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/news")
public class AdminNewsController {

  private final NewsRepository newsRepo;

  public AdminNewsController(NewsRepository newsRepo) {
    this.newsRepo = newsRepo;
  }

  @GetMapping
  public Page<AdminNewsDTO> list(@RequestParam(defaultValue = "active") String status,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "20") int size) {
    Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by("dateTime").descending());
    
    Page<News> newsPage;
    if ("all".equalsIgnoreCase(status)) {
      newsPage = newsRepo.findAll(pageable);
    } else {
      boolean hidden = "deleted".equalsIgnoreCase(status);
      newsPage = newsRepo.findByHidden(hidden, pageable);
    }
    
    // Convert to DTO to avoid circular reference issues
    return newsPage.map(this::toDTO);
  }

  private AdminNewsDTO toDTO(News n) {
    AdminNewsDTO dto = new AdminNewsDTO();
    dto.setId(n.getId());
    dto.setCategory(n.getCategory());
    dto.setHeadline(n.getHeadline());
    dto.setDetails(n.getDetails());
    dto.setReporter(n.getReporter());
    dto.setDateTime(n.getDateTime());
    dto.setImageUrl(n.getImageUrl());
    dto.setStatus(n.getStatus() != null ? n.getStatus().toString() : "UNKNOWN");
    dto.setVotesReal(n.getVotesReal());
    dto.setVotesFake(n.getVotesFake());
    dto.setHidden(n.isHidden());
    return dto;
  }

  // PATCH /api/admin/news/{id}/hide   (soft delete)
  @PatchMapping("/{id}/hide")
  public void hide(@PathVariable Long id) {
    News n = newsRepo.findById(id).orElseThrow();
    if (!n.isHidden()) {
      n.setHidden(true);
      newsRepo.save(n);
    }
  }

  // PATCH /api/admin/news/{id}/restore
  @PatchMapping("/{id}/restore")
  public void restore(@PathVariable Long id) {
    News n = newsRepo.findById(id).orElseThrow();
    if (n.isHidden()) {
      n.setHidden(false);
      newsRepo.save(n);
    }
  }
}