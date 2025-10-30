package com.legitnews.repository;
import com.legitnews.entity.CommentDeleteLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CommentDeleteLogRepository extends JpaRepository<CommentDeleteLog, Long> {
    Optional<CommentDeleteLog> findTopByCommentIdOrderByDeletedAtDesc(Long commentId);

}