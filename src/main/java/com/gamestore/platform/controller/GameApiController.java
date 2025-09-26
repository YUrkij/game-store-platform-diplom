package com.gamestore.platform.controller;

import com.gamestore.platform.dto.GameDTO;
import com.gamestore.platform.service.GameService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
public class GameApiController {

    private final GameService gameService;

    public GameApiController(GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/{id}")
    public GameDTO getGame(@PathVariable Long id) {
        return gameService.getGameById(id);
    }
}