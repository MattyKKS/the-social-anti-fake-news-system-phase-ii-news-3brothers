package com.legitnews.repository;

import com.legitnews.entity.Comment;
import com.legitnews.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> {
  Page<Comment> findByNews(News news, Pageable pageable);
}
