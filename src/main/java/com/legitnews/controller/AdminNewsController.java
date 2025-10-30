// src/main/java/com/legitnews/controller/AdminNewsController.java
package com.legitnews.controller;

import com.legitnews.entity.News;
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
public Page<News> list(@RequestParam(defaultValue = "active") String status,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "20") int size) {
  Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), Sort.by("dateTime").descending());
  if ("all".equalsIgnoreCase(status)) {
    return newsRepo.findAll(pageable);        // <-- NEW: return everything
  }
  boolean hidden = "deleted".equalsIgnoreCase(status);
  return newsRepo.findByHidden(hidden, pageable);
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
