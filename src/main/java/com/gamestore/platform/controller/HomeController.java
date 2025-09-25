package com.gamestore.platform.controller;

import com.gamestore.platform.dto.GameDTO;
import com.gamestore.platform.service.GameService;
import com.gamestore.platform.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final GameService gameService;
    private final UserService userService;

    public HomeController(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @GetMapping({"/", "/home"})
    public String home(@RequestParam(value = "genre", required = false) String genre,
                       @RequestParam(value = "search", required = false) String search,
                       Authentication authentication, Model model) {

        List<GameDTO> games;
        if (genre != null && !genre.isEmpty()) {
            games = gameService.getGamesByGenre(genre);
        } else if (search != null && !search.isEmpty()) {
            games = gameService.searchGames(search);
        } else {
            games = gameService.getAllGames();
        }

        model.addAttribute("games", games);

        // Добавляем информацию о текущем пользователе
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            model.addAttribute("currentUser", userService.getUserByUsername(username));
        }

        return "home";
    }
}
