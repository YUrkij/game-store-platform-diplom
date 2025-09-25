package com.gamestore.platform.controller;

import com.gamestore.platform.service.PurchaseService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final PurchaseService purchaseService;

    public CartController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping
    public String cart(Authentication authentication, Model model) {
        // Получаем корзину пользователя (можно хранить в сессии)
        // model.addAttribute("cartItems", cartService.getCart(userId));
        return "cart";
    }

    @PostMapping("/purchase")
    public String purchaseAll(Authentication authentication) {
        // Обрабатываем покупку всех товаров в корзине
        return "redirect:/user/profile?purchaseSuccess";
    }
}
