package com.legitnews.dao;

import com.legitnews.entity.*;
import org.springframework.data.domain.Page;

public interface NewsDao {
  Page<News> getNews(String q, String category, NewsStatus status, int page, int size);
  News getNews(Long id);
}
