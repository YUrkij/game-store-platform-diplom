package com.gamestore.platform.controller;

import com.gamestore.platform.service.GameService;
import com.gamestore.platform.service.PurchaseService;
import com.gamestore.platform.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/purchase")
public class PurchaseController {

    private final PurchaseService purchaseService;
    private final UserService userService;
    private final GameService gameService;

    public PurchaseController(PurchaseService purchaseService, UserService userService, GameService gameService) {
        this.purchaseService = purchaseService;
        this.userService = userService;
        this.gameService = gameService;
    }

    @GetMapping("/game/{gameId}")
    public String purchasePage(@PathVariable Long gameId, Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        var currentUser = userService.getUserByUsername(username);

        if (currentUser.isBanned()) {
            return "redirect:/games/" + gameId + "?error=banned";
        }

        // Проверяем, есть ли игра уже в библиотеке
        boolean alreadyOwned = purchaseService.hasGameInLibrary(currentUser.getId(), gameId);
        if (alreadyOwned) {
            return "redirect:/games/" + gameId + "?error=already_owned";
        }

        // Получаем полную информацию об игре
        var game = gameService.getGameById(gameId);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("game", game);
        model.addAttribute("gameId", gameId);

        return "purchase";
    }

    @PostMapping("/game/{gameId}")
    public String purchaseGame(@PathVariable Long gameId, Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        var currentUser = userService.getUserByUsername(username);

        if (currentUser.isBanned()) {
            redirectAttributes.addFlashAttribute("error", "Заблокированные пользователи не могут совершать покупки");
            return "redirect:/games/" + gameId;
        }

        try {
            purchaseService.purchaseGame(currentUser.getId(), gameId);
            redirectAttributes.addFlashAttribute("success", "Игра успешно приобретена! Теперь она доступна в вашей библиотеке.");
            return "redirect:/games/" + gameId;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/purchase/game/" + gameId;
        }
    }


}
