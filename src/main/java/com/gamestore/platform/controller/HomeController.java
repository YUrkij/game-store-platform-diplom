package com.gamestore.platform.controller;

import com.gamestore.platform.dto.GameDTO;
import com.gamestore.platform.dto.UserDTO;
import com.gamestore.platform.service.GameService;
import com.gamestore.platform.service.PurchaseService;
import com.gamestore.platform.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final GameService gameService;
    private final UserService userService;
    private final PurchaseService purchaseService;

    public HomeController(GameService gameService, UserService userService, PurchaseService purchaseService) {
        this.gameService = gameService;
        this.userService = userService;
        this.purchaseService = purchaseService;
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
        model.addAttribute("allGenres", gameService.getAllGenres());

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            UserDTO currentUser = userService.getUserByUsername(username);
            model.addAttribute("currentUser", currentUser);

            // Для каждой игры проверяем, есть ли она в библиотеке
            Map<Long, Boolean> gameInLibraryMap = new HashMap<>();
            for (GameDTO game : games) {
                boolean inLibrary = purchaseService.hasGameInLibrary(currentUser.getId(), game.getId());
                gameInLibraryMap.put(game.getId(), inLibrary);
            }
            model.addAttribute("gameInLibraryMap", gameInLibraryMap);
        }

        return "home";
    }
}
