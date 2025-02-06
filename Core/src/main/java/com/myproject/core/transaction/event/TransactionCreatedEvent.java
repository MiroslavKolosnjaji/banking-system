package com.myproject.core.transaction.event;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * @author Miroslav Kološnjaji
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionCreatedEvent {

    private Long transactionId;
    private Long userId;
    private String accountNumber;
    private BigDecimal amount;
    private BigDecimal balance;
    private String transactionType;
    private String status;
    private String description;
    private Instant createdAt;
}
