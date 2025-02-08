package com.myproject.transactionservice.dto;

import com.myproject.transactionservice.model.Status;
import com.myproject.transactionservice.model.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author Miroslav Kološnjaji
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {

    private Long id;
    private Long userId;
    private Long accountId;
    private BigDecimal amount;
    private String currency;
    private TransactionType transactionType;
    private Status status;
    private String description;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
