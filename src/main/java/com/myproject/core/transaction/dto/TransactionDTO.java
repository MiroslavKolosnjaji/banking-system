package com.myproject.core.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {

    private Long id;
    private Long userId;
    private Long accountId;
    private BigDecimal amount;
    private String transactionType;
    private String status;
    private String description;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}