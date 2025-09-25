package com.gamestore.platform.repository;

import com.gamestore.platform.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByTitleContainingIgnoreCase(String title);
    List<Game> findByDeveloperContainingIgnoreCase(String developer);

    @Query("SELECT g FROM Game g JOIN g.genres genre WHERE genre.name = :genreName")
    List<Game> findByGenreName(@Param("genreName") String genreName);

    @Query("SELECT g FROM Game g WHERE g.price BETWEEN :minPrice AND :maxPrice")
    List<Game> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);

    // Для загрузки игр с жанрами в одном запросе (решение проблемы N+1)
    @Query("SELECT DISTINCT g FROM Game g LEFT JOIN FETCH g.genres")
    List<Game> findAllWithGenres();

    @Query("SELECT g FROM Game g LEFT JOIN FETCH g.genres WHERE g.id = :id")
    Optional<Game> findByIdWithGenres(@Param("id") Long id);
}
