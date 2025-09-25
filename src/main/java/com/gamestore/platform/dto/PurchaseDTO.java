package com.gamestore.platform.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseDTO {
    private Long gameId;
    private Long userId;
    private BigDecimal purchasePrice;
}
