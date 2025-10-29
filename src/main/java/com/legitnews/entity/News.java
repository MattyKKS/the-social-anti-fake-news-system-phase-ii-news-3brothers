package com.legitnews.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="news")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class News {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank private String category;
  @NotBlank private String headline;

  @Column(length=4000)
  private String details;

  @NotBlank private String reporter;

  private LocalDateTime dateTime;     // parsed from "date"
  private String imageUrl;            // Firebase/public URL

  @Enumerated(EnumType.STRING)
  private NewsStatus status;

  private int votesReal;
  private int votesFake;

  @ElementCollection
  @CollectionTable(name = "news_real_voters", joinColumns = @JoinColumn(name = "news_id"))
  @Column(name = "user_id")
  private java.util.Set<Long> realVoterIds = new java.util.HashSet<>();

  @ElementCollection
  @CollectionTable(name = "news_fake_voters", joinColumns = @JoinColumn(name = "news_id"))
  @Column(name = "user_id")
  private java.util.Set<Long> fakeVoterIds = new java.util.HashSet<>();

  @OneToMany(mappedBy="news", cascade=CascadeType.ALL, orphanRemoval=true)
  private List<Comment> comments = new ArrayList<>();

  @ManyToOne(optional = true)                 
  @JoinColumn(name = "created_by_id")
  private User createdBy;
}
