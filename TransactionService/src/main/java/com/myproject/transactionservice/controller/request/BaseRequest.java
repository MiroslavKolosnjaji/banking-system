package com.myproject.transactionservice.controller.request;

import com.myproject.transactionservice.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * @author Miroslav Kološnjaji
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseRequest {

    @NotNull(message = "User ID required.")
    private Long userId;

    @NotNull(message = "Account ID required.")
    private Long accountId;

    @NotNull(message = "Amount required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0.")
    private BigDecimal amount;

    @NotNull(message = "Currency required.")
    @Size(min = 3, max = 3, message = "Currency must be exactly 3 characters")
    private String currency;

    @NotNull(message = "Transaction type required")
    private TransactionType transactionType;
}
