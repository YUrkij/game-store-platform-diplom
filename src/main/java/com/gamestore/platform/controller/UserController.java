package com.gamestore.platform.controller;

import com.gamestore.platform.dto.GameDTO;
import com.gamestore.platform.model.LibraryItem;
import com.gamestore.platform.service.GameService;
import com.gamestore.platform.service.PurchaseService;
import com.gamestore.platform.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final PurchaseService purchaseService;
    private final GameService gameService;

    public UserController(UserService userService, PurchaseService purchaseService, GameService gameService) {
        this.userService = userService;
        this.purchaseService = purchaseService;
        this.gameService = gameService;
    }

    @GetMapping("/profile")
    public String userProfile(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        var user = userService.getUserByUsername(username);
        model.addAttribute("user", user);
        model.addAttribute("currentUser", user);

        // Получаем библиотеку пользователя
        List<LibraryItem> libraryItems = purchaseService.getUserLibrary(user.getId());
        model.addAttribute("libraryItems", libraryItems);

        // Считаем общую сумму потраченных денег
        BigDecimal totalSpent = libraryItems.stream()
                .map(LibraryItem::getPurchasePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("totalSpent", totalSpent);

        return "user";
    }

    @GetMapping("/{userId}")
    public String viewUserProfile(@PathVariable Long userId, Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String currentUsername = authentication.getName();
        var currentUser = userService.getUserByUsername(currentUsername);
        model.addAttribute("currentUser", currentUser);

        var user = userService.getUserById(userId);
        model.addAttribute("user", user);

        // Проверяем права доступа
        if (!currentUser.getId().equals(userId) &&
                !currentUser.getRole().equals("ADMIN") &&
                !currentUser.getRole().equals("SUPERADMIN")) {
            return "redirect:/user/profile";
        }

        // Получаем библиотеку пользователя
        List<LibraryItem> libraryItems = purchaseService.getUserLibrary(userId);
        model.addAttribute("libraryItems", libraryItems);

        // Считаем общую сумму потраченных денег
        BigDecimal totalSpent = libraryItems.stream()
                .map(LibraryItem::getPurchasePrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("totalSpent", totalSpent);

        return "user";
    }

    @GetMapping("/balance")
    public String balancePage(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        var user = userService.getUserByUsername(username);

        if (user.isBanned()) {
            return "redirect:/user/profile?error=banned";
        }

        model.addAttribute("user", user);
        model.addAttribute("currentUser", user);
        return "balance";
    }

    @PostMapping("/balance")
    public String addBalance(@RequestParam Double amount, Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        var currentUser = userService.getUserByUsername(username);

        if (currentUser.isBanned()) {
            redirectAttributes.addFlashAttribute("error", "Заблокированные пользователи не могут пополнять баланс");
            return "redirect:/user/profile";
        }

        if (amount <= 0) {
            redirectAttributes.addFlashAttribute("error", "Сумма пополнения должна быть положительной");
            return "redirect:/user/balance";
        }

        try {
            userService.updateUserBalance(currentUser.getId(), amount);
            redirectAttributes.addFlashAttribute("success", "Баланс успешно пополнен на " + amount + " ₽");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/user/profile";
    }

    // Поиск пользователей для админов
    @GetMapping("/admin/search")
    public String userSearchPage(@RequestParam(value = "query", required = false) String query,
                                 Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        var currentUser = userService.getUserByUsername(username);
        model.addAttribute("currentUser", currentUser);

        // Проверяем права доступа
        if (!currentUser.getRole().equals("ADMIN") && !currentUser.getRole().equals("SUPERADMIN")) {
            return "redirect:/user/profile";
        }

        if (query != null && !query.trim().isEmpty()) {
            var searchResults = userService.searchUsers(query.trim());
            model.addAttribute("searchResults", searchResults);
            model.addAttribute("searchQuery", query);
        }

        return "admin_user_search";
    }

    // Страница создания игры для суперадминов
    @GetMapping("/superadmin/create-game")
    public String createGamePage(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        var currentUser = userService.getUserByUsername(username);
        model.addAttribute("currentUser", currentUser);

        // Проверяем права доступа
        if (!currentUser.getRole().equals("SUPERADMIN")) {
            return "redirect:/user/profile";
        }

        model.addAttribute("gameDTO", new GameDTO());
        return "superadmin_create_game";
    }

    @PostMapping("/superadmin/create-game")
    public String createGame(@ModelAttribute GameDTO gameDTO, Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        var currentUser = userService.getUserByUsername(username);

        // Проверяем права доступа
        if (!currentUser.getRole().equals("SUPERADMIN")) {
            return "redirect:/user/profile";
        }

        try {
            gameService.createGame(gameDTO);
            redirectAttributes.addFlashAttribute("success", "Игра '" + gameDTO.getTitle() + "' успешно создана!");
            return "redirect:/user/superadmin/create-game";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании игры: " + e.getMessage());
            redirectAttributes.addFlashAttribute("gameDTO", gameDTO);
            return "redirect:/user/superadmin/create-game";
        }
    }
}