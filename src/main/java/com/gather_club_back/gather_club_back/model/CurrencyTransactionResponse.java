package com.gather_club_back.gather_club_back.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class CurrencyTransactionResponse {
    private Integer transactionId;
    private Integer userId;
    private String username;
    private Integer amount;
    private String transactionType;
    private String description;
    private Integer referenceId;
    private LocalDateTime createdAt;
} 