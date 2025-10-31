package com.legitnews.dao;

import com.legitnews.entity.*;
import com.legitnews.repository.NewsRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Repository;

@Repository
public class NewsDaoImpl implements NewsDao {
  private final NewsRepository repo;
  public NewsDaoImpl(NewsRepository repo){ this.repo = repo; }

  @Override
  public Page<News> getNews(String q, String category, NewsStatus status, int page, int size) {
    Pageable pageable = PageRequest.of(Math.max(0,page), Math.max(1,size), Sort.by("dateTime").descending());
    return repo.search(emptyToNull(q), emptyToNull(category), status, pageable);
  }
  @Override
  public News getNews(Long id) { return repo.findById(id).filter(n -> !n.isHidden()).orElse(null); }
  private String emptyToNull(String s){ return (s==null || s.isBlank()) ? null : s; }
}
