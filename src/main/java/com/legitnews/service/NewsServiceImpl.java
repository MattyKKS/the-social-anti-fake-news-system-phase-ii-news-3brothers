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
import jakarta.transaction.Transactional;

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
  @Transactional
  public void vote(Long newsId, String value, Long userId) {
    if (userId == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
    }

    var n = newsRepo.findById(newsId)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "News not found"));

    final String v = value == null ? "" : value.trim().toLowerCase();
    final boolean toReal = switch (v) {
      case "real" -> true;
      case "fake" -> false;
      default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "value must be real|fake");
    };

    // already voted this way? no-op
    if (toReal && n.getRealVoterIds().contains(userId)) return;
    if (!toReal && n.getFakeVoterIds().contains(userId)) return;

    // if switching sides, remove from the other bucket and decrement
    if (n.getRealVoterIds().remove(userId)) {
      n.setVotesReal(Math.max(0, n.getVotesReal() - 1));
    }
    if (n.getFakeVoterIds().remove(userId)) {
      n.setVotesFake(Math.max(0, n.getVotesFake() - 1));
    }

    // add to target and increment
    if (toReal) {
      n.getRealVoterIds().add(userId);
      n.setVotesReal(n.getVotesReal() + 1);
    } else {
      n.getFakeVoterIds().add(userId);
      n.setVotesFake(n.getVotesFake() + 1);
    }

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
