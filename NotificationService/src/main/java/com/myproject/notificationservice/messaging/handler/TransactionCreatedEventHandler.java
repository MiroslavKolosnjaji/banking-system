package com.myproject.notificationservice.messaging.handler;


import com.myproject.core.transaction.event.TransactionCreatedEvent;
import com.myproject.notificationservice.dto.TransactionNotificationDTO;
import com.myproject.notificationservice.exception.service.NotificationNotFoundException;
import com.myproject.notificationservice.repository.TransactionRepository;
import com.myproject.notificationservice.service.TransactionNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author Miroslav Kološnjaji
 */
@Slf4j
@Component
@KafkaListener(topics = "${kafka.topic.name}")
@RequiredArgsConstructor
public class TransactionCreatedEventHandler {

    private final TransactionNotificationService transactionNotificationService;

    @KafkaHandler
    public void handleEvent(@Payload TransactionCreatedEvent transactionCreatedEvent, @Header("messageId") String messageId) throws NotificationNotFoundException {

        log.info("TRANSACTION CREATED EVENT SUCCESSFULLY DESERIALIZED: {}", transactionCreatedEvent);


        Optional<TransactionNotificationDTO> transactionNotificationDTO = transactionNotificationService.getTransactionNotificationByMessageId(messageId);

        if (transactionNotificationDTO.isEmpty())
            saveEvent(messageId, transactionCreatedEvent);
        else
            switch (transactionNotificationDTO.get().getEmailStatus()) {
                case PENDING -> transactionNotificationService.retryToSendEmail(messageId);
                case DELIVERED -> log.info("Message with ID {} already processed. Skipping processing.", messageId);
                default -> log.error("Not found valid email status.");
            }

    }

    private void saveEvent(String messageId, TransactionCreatedEvent transactionCreatedEvent) throws NotificationNotFoundException {

        log.info("Successfully handled event:");
        log.info(" ");
        log.info("*********Transaction Created Event*********");
        log.info("Transaction ID: {}", transactionCreatedEvent.getTransactionId());
        log.info("User ID: {}", transactionCreatedEvent.getUserId());
        log.info("Email: {}", transactionCreatedEvent.getEmail());
        log.info("Account number: {}", transactionCreatedEvent.getAccountNumber());
        log.info("Amount: {}", transactionCreatedEvent.getAmount());
        log.info("Balance: {}", transactionCreatedEvent.getBalance());
        log.info("Currency: {}", transactionCreatedEvent.getCurrency());
        log.info("Transaction type: {}", transactionCreatedEvent.getTransactionType());
        log.info("Status: {}", transactionCreatedEvent.getStatus());
        log.info("Description: {}", transactionCreatedEvent.getDescription());
        log.info("Created at: {}", transactionCreatedEvent.getCreatedAt());


        TransactionNotificationDTO transactionNotificationDTO = TransactionNotificationDTO.builder()
                .transactionId(transactionCreatedEvent.getTransactionId())
                .userId(transactionCreatedEvent.getUserId())
                .accountNumber(transactionCreatedEvent.getAccountNumber())
                .email(transactionCreatedEvent.getEmail())
                .amount(transactionCreatedEvent.getAmount())
                .currency(transactionCreatedEvent.getCurrency())
                .balance(transactionCreatedEvent.getBalance())
                .transactionType(transactionCreatedEvent.getTransactionType())
                .status(transactionCreatedEvent.getStatus())
                .description(transactionCreatedEvent.getDescription())
                .createdAt(transactionCreatedEvent.getCreatedAt())
                .build();

        transactionNotificationService.save(messageId, transactionNotificationDTO);
    }

}
