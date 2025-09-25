package com.gamestore.platform.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Data
public class GameDTO {
    private Long id;

    @NotBlank(message = "Название игры обязательно")
    private String title;

    private String description;

    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.0", message = "Цена не может быть отрицательной")
    private BigDecimal price;

    private LocalDate releaseDate;
    private String developer;
    private String publisher;
    private String imageUrl;
    private Set<String> genres;
}