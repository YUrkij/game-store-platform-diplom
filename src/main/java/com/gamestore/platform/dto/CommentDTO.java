package com.gamestore.platform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private Long id;

    @NotBlank(message = "Комментарий не может быть пустым")
    private String commentText;

    private Long gameId;
    private Long userId;
    private String username;
    private String userRole;
    private LocalDateTime createdAt;
}
