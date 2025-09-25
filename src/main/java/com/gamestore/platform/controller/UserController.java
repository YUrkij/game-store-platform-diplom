package com.gamestore.platform.controller;

import com.gamestore.platform.service.PurchaseService;
import com.gamestore.platform.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final PurchaseService purchaseService;

    public UserController(UserService userService, PurchaseService purchaseService) {
        this.userService = userService;
        this.purchaseService = purchaseService;
    }

    @GetMapping("/profile")
    public String userProfile(Authentication authentication, Model model) {
        String username = authentication.getName();
        model.addAttribute("user", userService.getUserByUsername(username));
        return "user";
    }

    @PostMapping("/balance")
    public String addBalance(@RequestParam Double amount, Authentication authentication) {
        // Получаем userId из authentication
        // userService.updateUserBalance(userId, amount);
        return "redirect:/user/profile";
    }
}