// src/main/java/com/legitnews/repository/NewsRepository.java
package com.legitnews.repository;

import com.legitnews.entity.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface NewsRepository extends JpaRepository<News,Long> {

  @Query("""
    SELECT n FROM News n
    WHERE (:q IS NULL OR LOWER(n.headline) LIKE LOWER(CONCAT('%',:q,'%'))
           OR LOWER(n.details)  LIKE LOWER(CONCAT('%',:q,'%'))
           OR LOWER(n.reporter) LIKE LOWER(CONCAT('%',:q,'%')))
      AND (:cat IS NULL OR n.category = :cat)
      AND (:st IS NULL OR n.status = :st)
  """)
  Page<News> search(@Param("q") String q,
                    @Param("cat") String category,
                    @Param("st") NewsStatus st,
                    Pageable pageable);

  Optional<News> findByHeadlineAndDateTime(String headline, LocalDateTime dateTime);
}
