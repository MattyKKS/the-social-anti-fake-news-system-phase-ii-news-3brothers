// src/main/java/com/legitnews/config/JsonImportConfig.java
package com.legitnews.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.legitnews.entity.*;
import com.legitnews.repository.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Configuration
public class JsonImportConfig {

  @Value("${app.import.force:false}")
  boolean force;

  @Bean
  CommandLineRunner importAll(NewsRepository newsRepo,
                              UserRepository userRepo,
                              CommentRepository commentRepo) {
    return args -> {
      // read db.json first
      ObjectMapper mapper = new ObjectMapper();
      InputStream is = getClass().getResourceAsStream("/db.json");
      if (is == null) {
        System.out.println("db.json not found; skipping import.");
        return;
      }
      List<JsonNews> items = mapper.readValue(is, new TypeReference<>() {});
      DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

      if (force) {
        // wipe (safe order because of FK)
        commentRepo.deleteAll();
        newsRepo.deleteAll();
        // keep users, or recreate anon below
      } else if (newsRepo.count() == 0) {
        // ok, fresh DB; proceed
      } else {
        // UPSERT mode; continue and merge row-by-row
        System.out.println("DB has data; running in UPSERT mode (no truncation).");
      }

      // ensure Anonymous user exists
      var anon = userRepo.findByEmail("anonymous@local")
          .orElseGet(() -> userRepo.save(User.builder()
              .name("Anonymous")
              .email("anonymous@local")
              .role(Role.READER)
              .createdAt(LocalDateTime.now())
              .build()));

      long imported = 0;
      long updated  = 0;
      long comments = 0;

      for (JsonNews j : items) {
        LocalDateTime dt;
        try {
          dt = (j.date == null || j.date.isBlank()) ? LocalDateTime.now() : LocalDateTime.parse(j.date, fmt);
        } catch (Exception e) {
          dt = LocalDateTime.now();
        }

        // keep current path for now; switch to Firebase URLs later after upload
        String imageUrl = (j.image != null && (j.image.startsWith("http://") || j.image.startsWith("https://")))
            ? j.image
            : ("/images/" + encode(j.category) + "/" + safe(j.image));

        var headline = safe(j.headline, safe(j.title, "Untitled"));

        // find existing by natural key
        var existingOpt = newsRepo.findByHeadlineAndDateTime(headline, dt);
        News news = existingOpt.orElseGet(() -> News.builder().build());

        // set/overwrite fields (UPSERT)
        news.setCategory(safe(j.category, "General"));
        news.setHeadline(headline);
        news.setDetails(safe(j.details, safe(j.detail, "")));
        news.setReporter(safe(j.reporter, "Anonymous"));
        news.setDateTime(dt);
        news.setImageUrl(imageUrl);
        news.setStatus(NewsStatus.UNKNOWN);
        news.setVotesReal(j.votes != null ? j.votes.real : 0);
        news.setVotesFake(j.votes != null ? j.votes.fake : 0);

        boolean wasNew = (news.getId() == null);
        news = newsRepo.save(news);
        if (wasNew) imported++; else updated++;

        // if FORCE or wasNew, (re)create comments from JSON; if UPSERT on existing rows, skip to avoid duplicates
        if (j.comments != null && (force || wasNew)) {
          // delete existing comments if force (to avoid duplicates)
          if (!wasNew && force) {
            commentRepo.findByNews(news, org.springframework.data.domain.PageRequest.of(0, 1000))
                .forEach(c -> commentRepo.delete(c));
          }
          for (String c : j.comments) {
            if (c == null || c.isBlank()) continue;
            commentRepo.save(Comment.builder()
                .news(news)
                .user(anon)
                .content(c.trim())
                .createdAt(LocalDateTime.now())
                .build());
            comments++;
          }
        }
      }
      System.out.printf("Import summary: new=%d, updated=%d, comments=%d%n", imported, updated, comments);
    };
  }

  private static String encode(String s) {
    try { return java.net.URLEncoder.encode(s == null ? "General" : s, java.nio.charset.StandardCharsets.UTF_8); }
    catch (Exception e) { return "General"; }
  }
  private static String safe(String s) { return s == null ? "" : s; }
  private static String safe(String s, String def) { return (s == null || s.isBlank()) ? def : s; }

  // JSON DTOs
  @Data @NoArgsConstructor
  static class JsonNews {
    public String category;
    public Integer id;       // legacy
    public String headline;
    public String title;
    public String details;
    public String detail;
    public String reporter;
    public String date;      // "yyyy-MM-dd HH:mm"
    public String image;
    public Votes votes;
    public List<String> comments;
  }
  @Data @NoArgsConstructor
  static class Votes { public int real; public int fake; }
}
