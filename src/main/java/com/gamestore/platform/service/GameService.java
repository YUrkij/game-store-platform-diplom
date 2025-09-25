package com.gamestore.platform.service;

import com.gamestore.platform.dto.GameDTO;
import com.gamestore.platform.model.Game;
import com.gamestore.platform.model.Genre;
import com.gamestore.platform.repository.GameRepository;
import com.gamestore.platform.repository.GenreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class GameService {

    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;

    public GameService(GameRepository gameRepository, GenreRepository genreRepository) {
        this.gameRepository = gameRepository;
        this.genreRepository = genreRepository;
    }

    public List<GameDTO> getAllGames() {
        return gameRepository.findAllWithGenres().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public GameDTO getGameById(Long id) {
        Game game = gameRepository.findByIdWithGenres(id)
                .orElseThrow(() -> new IllegalArgumentException("Игра не найдена"));
        return convertToDTO(game);
    }

    public List<GameDTO> getGamesByGenre(String genreName) {
        return gameRepository.findByGenreName(genreName).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<GameDTO> searchGames(String query) {
        return gameRepository.findByTitleContainingIgnoreCase(query).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public GameDTO createGame(GameDTO gameDTO) {
        Game game = new Game();
        game.setTitle(gameDTO.getTitle());
        game.setDescription(gameDTO.getDescription());
        game.setPrice(gameDTO.getPrice());
        game.setReleaseDate(gameDTO.getReleaseDate());
        game.setDeveloper(gameDTO.getDeveloper());
        game.setPublisher(gameDTO.getPublisher());
        game.setImageUrl(gameDTO.getImageUrl());

        // Устанавливаем жанры
        if (gameDTO.getGenres() != null) {
            Set<Genre> genres = gameDTO.getGenres().stream()
                    .map(genreName -> genreRepository.findByName(genreName)
                            .orElseGet(() -> {
                                Genre newGenre = new Genre();
                                newGenre.setName(genreName);
                                return genreRepository.save(newGenre);
                            }))
                    .collect(Collectors.toSet());
            game.setGenres(genres);
        }

        Game savedGame = gameRepository.save(game);
        return convertToDTO(savedGame);
    }

    public GameDTO updateGame(Long id, GameDTO gameDTO) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Игра не найдена"));

        game.setTitle(gameDTO.getTitle());
        game.setDescription(gameDTO.getDescription());
        game.setPrice(gameDTO.getPrice());
        // ... остальные поля

        Game updatedGame = gameRepository.save(game);
        return convertToDTO(updatedGame);
    }

    public void deleteGame(Long id) {
        gameRepository.deleteById(id);
    }

    private GameDTO convertToDTO(Game game) {
        GameDTO dto = new GameDTO();
        dto.setId(game.getId());
        dto.setTitle(game.getTitle());
        dto.setDescription(game.getDescription());
        dto.setPrice(game.getPrice());
        dto.setReleaseDate(game.getReleaseDate());
        dto.setDeveloper(game.getDeveloper());
        dto.setPublisher(game.getPublisher());
        dto.setImageUrl(game.getImageUrl());

        if (game.getGenres() != null) {
            dto.setGenres(game.getGenres().stream()
                    .map(Genre::getName)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }
}
