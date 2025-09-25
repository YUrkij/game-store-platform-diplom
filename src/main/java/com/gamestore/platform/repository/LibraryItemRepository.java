package com.gamestore.platform.repository;

import com.gamestore.platform.model.LibraryItem;
import com.gamestore.platform.model.LibraryItemPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LibraryItemRepository extends JpaRepository<LibraryItem, LibraryItemPK> {

    // Исправляем запрос - используем прямые связи вместо pk
    @Query("SELECT li FROM LibraryItem li JOIN FETCH li.game WHERE li.id.userId = :userId")
    List<LibraryItem> findByUserIdWithGames(@Param("userId") Long userId);

    @Query("SELECT li FROM LibraryItem li WHERE li.id.userId = :userId AND li.id.gameId = :gameId")
    Optional<LibraryItem> findByUserIdAndGameId(@Param("userId") Long userId, @Param("gameId") Long gameId);

    boolean existsById_UserIdAndId_GameId(Long userId, Long gameId);

    @Query("SELECT COUNT(li) FROM LibraryItem li WHERE li.id.gameId = :gameId")
    Long countByGameId(@Param("gameId") Long gameId);

    // Добавляем простые методы для поиска по userId
    List<LibraryItem> findById_UserId(Long userId);
}
