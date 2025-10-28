package com.legitnews.service;

import com.legitnews.dao.NewsDao;
import com.legitnews.dto.NewsDTO;
import com.legitnews.entity.News;
import com.legitnews.entity.NewsStatus;
import com.legitnews.repository.NewsRepository;
import com.legitnews.util.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {
  private final NewsDao dao;
  private final Mappers mappers;
  private final NewsRepository newsRepo; // <-- add this line

  // ✅ Updated constructor: include NewsRepository
  public NewsServiceImpl(NewsDao dao, Mappers mappers, NewsRepository newsRepo) {
    this.dao = dao;
    this.mappers = mappers;
    this.newsRepo = newsRepo;
  }

  @Override
  public Page<NewsDTO> list(String q, String category, NewsStatus st, int page, int size) {
    var p = dao.getNews(q, category, st, page, size);
    var dtoList = p.getContent().stream().map(mappers::toDTO).collect(Collectors.toList());
    return new PageImpl<>(dtoList, p.getPageable(), p.getTotalElements());
  }

  @Override
  public NewsDTO get(Long id) {
    News n = dao.getNews(id);
    if (n == null) throw new RuntimeException("News not found");
    return mappers.toDTO(n);
  }

  @Override
  public void vote(Long id, String value) {
    var n = newsRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found"));

    String v = (value == null) ? "" : value.trim().toLowerCase();

    if ("real".equals(v)) n.setVotesReal(n.getVotesReal() + 1);
    else if ("fake".equals(v)) n.setVotesFake(n.getVotesFake() + 1);
    else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "value must be real|fake");

    newsRepo.save(n);
  }
}
