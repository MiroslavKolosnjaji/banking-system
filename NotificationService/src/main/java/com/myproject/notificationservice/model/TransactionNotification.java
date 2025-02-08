package com.myproject.notificationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * @author Miroslav Kološnjaji
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document
public class TransactionNotification {

    @Id
    private String id;

    private String messageId;
    private Long transactionId;
    private Long userId;
    private String accountNumber;
    private BigDecimal amount;
    private BigDecimal balance;
    private String transactionType;
    private String description;
    private String status;
    private NotificationType notificationType;

    @CreatedDate
    private Instant createdAt;
}
