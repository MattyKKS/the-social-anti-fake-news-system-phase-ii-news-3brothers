package com.legitnews.service;

import com.legitnews.dao.NewsDao;
import com.legitnews.dto.NewsDTO;
import com.legitnews.entity.News;
import com.legitnews.entity.NewsStatus;
import com.legitnews.util.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {
  private final NewsDao dao;
  public NewsServiceImpl(NewsDao dao){ this.dao = dao; }

  @Override
  public Page<NewsDTO> list(String q, String category, NewsStatus st, int page, int size) {
    var p = dao.getNews(q, category, st, page, size);
    var dtoList = p.getContent().stream().map(Mappers::toDTO).collect(Collectors.toList());
    return new PageImpl<>(dtoList, p.getPageable(), p.getTotalElements());
  }
  @Override
  public NewsDTO get(Long id) {
    News n = dao.getNews(id);
    if(n==null) throw new RuntimeException("News not found");
    return Mappers.toDTO(n);
  }
}
