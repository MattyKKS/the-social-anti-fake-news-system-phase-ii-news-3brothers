package com.legitnews.service;

import com.legitnews.dto.NewsDTO;
import com.legitnews.entity.NewsStatus;
import org.springframework.data.domain.Page;

public interface NewsService {
  Page<NewsDTO> list(String q, String category, NewsStatus status, int page, int size);
  NewsDTO get(Long id);
}
