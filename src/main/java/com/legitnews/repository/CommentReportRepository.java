// src/main/java/com/legitnews/repo/CommentReportRepository.java
package com.legitnews.repository;
import com.legitnews.entity.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {}