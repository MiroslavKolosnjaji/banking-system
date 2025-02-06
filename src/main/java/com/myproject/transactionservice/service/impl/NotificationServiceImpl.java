package com.myproject.transactionservice.service.impl;

import com.myproject.core.transaction.event.TransactionCreatedEvent;
import com.myproject.transactionservice.dto.TransactionDTO;
import com.myproject.transactionservice.dto.TransactionDetailsDTO;
import com.myproject.transactionservice.messaging.NotificationPublisher;
import com.myproject.transactionservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.concurrent.ExecutionException;

/**
 * @author Miroslav Kološnjaji
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationPublisher notificationPublisher;

    @Override
    public void sendNotification(TransactionDetailsDTO transactionDetailsDTO, TransactionDTO transactionDTO) throws ExecutionException, InterruptedException {

        TransactionCreatedEvent transactionCreatedEvent = TransactionCreatedEvent.builder()
                .transactionId(transactionDTO.getId())
                .userId(transactionDTO.getUserId())
                .amount(transactionDTO.getAmount())
                .transactionType(String.valueOf(transactionDTO.getTransactionType()))
                .status(String.valueOf(transactionDTO.getStatus()))
                .createdAt(transactionDTO.getCreatedAt().toInstant())
                .accountNumber(transactionDetailsDTO.getAccountNumber())
                .balance(transactionDetailsDTO.getBalance())
                .description(transactionDetailsDTO.getDescription())
                .build();

        notificationPublisher.send(transactionCreatedEvent);

    }

}
