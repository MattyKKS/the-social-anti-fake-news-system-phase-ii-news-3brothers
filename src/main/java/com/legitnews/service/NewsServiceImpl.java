package com.legitnews.service;

import com.legitnews.dao.NewsDao;
import com.legitnews.dto.CreateNewsRequest;
import com.legitnews.dto.NewsDTO;
import com.legitnews.entity.News;
import com.legitnews.entity.NewsStatus;
import com.legitnews.entity.User;                 // ✅ add this
import com.legitnews.repository.NewsRepository;
import com.legitnews.repository.UserRepository;
import com.legitnews.util.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;                  // ✅ add this
import java.time.OffsetDateTime;                // ✅ add this (for robust parse)
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {
  private final NewsDao dao;
  private final Mappers mappers;
  private final NewsRepository newsRepo;
  private final UserRepository userRepo;

  public NewsServiceImpl(NewsDao dao, Mappers mappers, NewsRepository newsRepo, UserRepository userRepo) {
    this.dao = dao;
    this.mappers = mappers;
    this.newsRepo = newsRepo;
    this.userRepo = userRepo;
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
    String v = value == null ? "" : value.trim().toLowerCase();
    if ("real".equals(v)) n.setVotesReal(n.getVotesReal() + 1);
    else if ("fake".equals(v)) n.setVotesFake(n.getVotesFake() + 1);
    else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "value must be real|fake");
    newsRepo.save(n);
  }

  @Override
  public NewsDTO create(CreateNewsRequest req) {
    // ✅ resolve creator (nullable)
    User creator = null;
    if (req.getCreatedById() != null) {
      creator = userRepo.findById(req.getCreatedById()).orElse(null);
    }

    // ✅ parse date string -> LocalDateTime (robust to ISO or ISO-Offset)
    LocalDateTime dt = LocalDateTime.now();
    if (req.getDateTime() != null && !req.getDateTime().isBlank()) {
      try {
        dt = LocalDateTime.parse(req.getDateTime());
      } catch (Exception e1) {
        try {
          dt = OffsetDateTime.parse(req.getDateTime()).toLocalDateTime();
        } catch (Exception e2) {
          // fallback to now; or throw if you prefer strict
          dt = LocalDateTime.now();
        }
      }
    }

    News n = News.builder()
        .category(req.getCategory())
        .headline(req.getHeadline())
        .details(req.getDetails())
        .reporter(req.getReporter())
        .dateTime(dt)                         // ✅ pass LocalDateTime
        .imageUrl(req.getImageUrl())
        .status(NewsStatus.UNKNOWN)           // default
        .createdBy(creator)                   // ✅ store creator
        .build();

    n = newsRepo.save(n);
    return mappers.toDTO(n);
  }
}
