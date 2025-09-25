package com.gamestore.platform.controller;

import com.gamestore.platform.dto.CommentDTO;
import com.gamestore.platform.dto.GameDTO;
import com.gamestore.platform.service.CommentService;
import com.gamestore.platform.service.GameService;
import com.gamestore.platform.service.PurchaseService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;
    private final CommentService commentService;
    private final PurchaseService purchaseService;

    public GameController(GameService gameService, CommentService commentService, PurchaseService purchaseService) {
        this.gameService = gameService;
        this.commentService = commentService;
        this.purchaseService = purchaseService;
    }

    @GetMapping("/{id}")
    public String gameDetails(@PathVariable Long id, Authentication authentication, Model model) {
        GameDTO game = gameService.getGameById(id);
        model.addAttribute("game", game);
        model.addAttribute("comments", commentService.getCommentsByGameId(id));

        if (authentication != null) {
            // Проверяем, есть ли игра в библиотеке пользователя
            // boolean inLibrary = purchaseService.hasGameInLibrary(userId, id);
            // model.addAttribute("inLibrary", inLibrary);
        }

        return "game";
    }

    @PostMapping("/{id}/comment")
    public String addComment(@PathVariable Long id, @RequestParam String commentText,
                             Authentication authentication) {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setGameId(id);
        commentDTO.setCommentText(commentText);
        // commentDTO.setUserId(/* получить из authentication */);

        commentService.addComment(commentDTO);
        return "redirect:/games/" + id;
    }

    @PostMapping("/{id}/purchase")
    public String purchaseGame(@PathVariable Long id, Authentication authentication) {
        // purchaseService.purchaseGame(userId, id);
        return "redirect:/cart";
    }
}
