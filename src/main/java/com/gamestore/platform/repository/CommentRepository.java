// CommentRepository.java
package com.gamestore.platform.repository;

import com.gamestore.platform.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByGameIdOrderByCreatedAtDesc(Long gameId);
    List<Comment> findByUserId(Long userId);

    @Query("SELECT c FROM Comment c WHERE c.game.id = :gameId AND c.user.id = :userId")
    Optional<Comment> findByGameIdAndUserId(@Param("gameId") Long gameId, @Param("userId") Long userId);

    boolean existsByGameIdAndUserId(Long gameId, Long userId);
}
