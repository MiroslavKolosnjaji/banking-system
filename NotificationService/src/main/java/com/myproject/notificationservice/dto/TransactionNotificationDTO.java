package com.myproject.notificationservice.dto;

import com.myproject.notificationservice.model.EmailStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * @author Miroslav Kološnjaji
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionNotificationDTO {

    //PUT VALIDATION!

    private String id;

    private Long transactionId;
    private Long userId;
    private String email;
    private String accountNumber;
    private BigDecimal amount;
    private BigDecimal balance;
    private String currency;
    private String transactionType;
    private String status;
    private String description;
    private EmailStatus emailStatus;
    private Instant createdAt;
}
