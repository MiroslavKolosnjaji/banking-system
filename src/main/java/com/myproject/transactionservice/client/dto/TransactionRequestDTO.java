package com.myproject.transactionservice.client.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Miroslav Kološnjaji
 */
@Data
public abstract class TransactionRequestDTO {

    @NotNull(message = "User ID required")
    private Long userId;

    @NotNull(message = "Amount required")
    @Positive(message = "Amount must be greater than zero")
    private BigDecimal amount;

}
