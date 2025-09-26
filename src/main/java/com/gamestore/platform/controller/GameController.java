package com.gamestore.platform.controller;

import com.gamestore.platform.dto.CommentDTO;
import com.gamestore.platform.dto.GameDTO;
import com.gamestore.platform.dto.UserDTO;
import com.gamestore.platform.service.CommentService;
import com.gamestore.platform.service.GameService;
import com.gamestore.platform.service.PurchaseService;
import com.gamestore.platform.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;
    private final CommentService commentService;
    private final PurchaseService purchaseService;
    private final UserService userService;

    public GameController(GameService gameService, CommentService commentService,
                          PurchaseService purchaseService, UserService userService) {
        this.gameService = gameService;
        this.commentService = commentService;
        this.purchaseService = purchaseService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public String gameDetails(@PathVariable Long id, Authentication authentication, Model model) {
        GameDTO game = gameService.getGameById(id);
        model.addAttribute("game", game);
        model.addAttribute("comments", commentService.getCommentsByGameId(id));

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            UserDTO currentUser = userService.getUserByUsername(username);
            model.addAttribute("currentUser", currentUser);

            // Проверяем, есть ли игра в библиотеке пользователя
            boolean inLibrary = purchaseService.hasGameInLibrary(currentUser.getId(), id);
            model.addAttribute("inLibrary", inLibrary);

            // Получаем комментарий пользователя для этой игры (если есть)
            CommentDTO userComment = commentService.getCommentByUserAndGame(currentUser.getId(), id);
            model.addAttribute("userComment", userComment);
        }

        return "game";
    }

    @PostMapping("/{id}/comment")
    public String addComment(@PathVariable Long id, @RequestParam String commentText,
                             Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        UserDTO currentUser = userService.getUserByUsername(username);

        // Проверяем, не забанен ли пользователь
        if (currentUser.isBanned()) {
            redirectAttributes.addFlashAttribute("error", "Заблокированные пользователи не могут оставлять комментарии");
            return "redirect:/game/" + id;
        }

        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setGameId(id);
        commentDTO.setCommentText(commentText);
        commentDTO.setUserId(currentUser.getId());

        try {
            commentService.addComment(commentDTO);
            redirectAttributes.addFlashAttribute("success", "Комментарий успешно добавлен");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/game/" + id;
    }

    @PostMapping("/{id}/comment/update")
    public String updateComment(@PathVariable Long id, @RequestParam String commentText,
                                Authentication authentication, RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        UserDTO currentUser = userService.getUserByUsername(username);

        try {
            // Находим комментарий пользователя для этой игры
            CommentDTO existingComment = commentService.getCommentByUserAndGame(currentUser.getId(), id);
            if (existingComment == null) {
                redirectAttributes.addFlashAttribute("error", "Комментарий не найден");
                return "redirect:/game/" + id;
            }

            commentService.updateComment(existingComment.getId(), currentUser.getId(), commentText);
            redirectAttributes.addFlashAttribute("success", "Комментарий успешно обновлен");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/game/" + id;
    }

    @PostMapping("/{id}/comment/delete")
    public String deleteComment(@PathVariable Long id, Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String username = authentication.getName();
        UserDTO currentUser = userService.getUserByUsername(username);

        try {
            // Находим комментарий пользователя для этой игры
            CommentDTO existingComment = commentService.getCommentByUserAndGame(currentUser.getId(), id);
            if (existingComment == null) {
                redirectAttributes.addFlashAttribute("error", "Комментарий не найден");
                return "redirect:/games/" + id;
            }

            commentService.deleteComment(existingComment.getId(), currentUser.getId());
            redirectAttributes.addFlashAttribute("success", "Комментарий успешно удален");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/game/" + id;
    }

}