package com.legitnews.controller;

import com.legitnews.dto.NewsDTO;
import com.legitnews.entity.NewsStatus;
import com.legitnews.service.NewsService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/news")
public class NewsController {
  private final NewsService service;
  public NewsController(NewsService service){ this.service = service; }

  @GetMapping
  public Page<NewsDTO> list(@RequestParam(required=false) String q,
                            @RequestParam(required=false) String category,
                            @RequestParam(required=false) NewsStatus status,
                            @RequestParam(defaultValue="0") int page,
                            @RequestParam(defaultValue="10") int size) {
    return service.list(q, category, status, page, size);
  }

  @GetMapping("/{id}")
  public NewsDTO get(@PathVariable Long id){ return service.get(id); }

  @PostMapping("/{id}/vote")
  public void vote(@PathVariable Long id, @RequestParam String value, @RequestParam Long userId) {
    service.vote(id, value, userId); // updates DB; no response body needed
  }
}
