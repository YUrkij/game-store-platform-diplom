package com.gamestore.platform.service;

import com.gamestore.platform.dto.CommentDTO;
import com.gamestore.platform.model.Comment;
import com.gamestore.platform.model.Game;
import com.gamestore.platform.model.User;
import com.gamestore.platform.repository.CommentRepository;
import com.gamestore.platform.repository.GameRepository;
import com.gamestore.platform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository, GameRepository gameRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    public List<CommentDTO> getCommentsByGameId(Long gameId) {
        return commentRepository.findByGameIdOrderByCreatedAtDesc(gameId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CommentDTO addComment(CommentDTO commentDTO) {
        User user = userRepository.findById(commentDTO.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (user.getIsBanned()) {
            throw new IllegalArgumentException("Заблокированный пользователь не может оставлять комментарии");
        }

        Game game = gameRepository.findById(commentDTO.getGameId())
                .orElseThrow(() -> new IllegalArgumentException("Игра не найдена"));

        // Проверяем, не оставлял ли пользователь уже комментарий к этой игре
        if (commentRepository.existsByGameIdAndUserId(game.getId(), user.getId())) {
            throw new IllegalArgumentException("Можно оставить только один комментарий к игре");
        }

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setGame(game);
        comment.setCommentText(commentDTO.getCommentText());

        Comment savedComment = commentRepository.save(comment);
        return convertToDTO(savedComment);
    }

    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public void deleteCommentsByUserId(Long userId) {
        List<Comment> comments = commentRepository.findByUserId(userId);
        commentRepository.deleteAll(comments);
    }

    private CommentDTO convertToDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setCommentText(comment.getCommentText());
        dto.setGameId(comment.getGame().getId());
        dto.setUserId(comment.getUser().getId());
        dto.setUsername(comment.getUser().getUsername());
        dto.setUserRole(comment.getUser().getRole().getName());
        dto.setCreatedAt(comment.getCreatedAt());
        return dto;
    }
}
