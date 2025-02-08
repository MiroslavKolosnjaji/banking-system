package com.myproject.transactionservice.controller.request;

import com.myproject.transactionservice.model.Status;
import com.myproject.transactionservice.model.TransactionType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.sql.Update;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author Miroslav Kološnjaji
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {

    @NotNull(groups = Update.class)
    private Long id;

    @NotNull(message = "User id is required")
    private Long userId;

    @NotNull(message = "Account id is required")
    private Long accountId;

    @NotNull(message = "Amount required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0.")
    private BigDecimal amount;

    @NotEmpty(message = "Currency required")
    @Size(min = 3, max = 3, message = "Currency must be exactly 3 characters")
    private String currency;

    @NotNull(message = "Transaction type required")
    private TransactionType transactionType;

    @NotNull(groups = Update.class, message = "Status is required")
    private Status status;

    @NotNull(groups = Update.class, message = "Description is required")
    @Size(max = 125, message = "Description must not exceed 125 characters.")
    private String description;

    @NotNull(groups = Update.class, message = "Created date required")
    private Timestamp createdAt;

    @NotNull(groups = Update.class, message = "Last modified date required")
    private Timestamp updatedAt;
}
