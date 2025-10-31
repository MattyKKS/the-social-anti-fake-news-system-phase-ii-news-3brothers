// src/main/java/com/legitnews/controller/NewsWriteController.java
package com.legitnews.controller;

import com.legitnews.dto.CreateNewsRequest;
import com.legitnews.dto.NewsDTO;
import com.legitnews.service.NewsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/news")
public class NewsWriteController {
  private final NewsService svc;
  public NewsWriteController(NewsService svc){ this.svc = svc; }

  @PostMapping
  public NewsDTO create(@RequestBody CreateNewsRequest req) {
    return svc.create(req);
  }
}
