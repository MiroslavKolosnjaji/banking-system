package com.myproject.transactionservice.messaging;

import com.myproject.core.transaction.event.TransactionCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * @author Miroslav Kološnjaji
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationPublisher {


    @Value("${kafka.topic.name}")
    private String topicName;

    private final KafkaTemplate<String, TransactionCreatedEvent> kafkaTemplate;

    public void send (TransactionCreatedEvent transactionCreatedEvent) throws ExecutionException, InterruptedException {

        log.info("Transaction id: {}", transactionCreatedEvent.getTransactionId());
        log.info("Account number: {}", transactionCreatedEvent.getAccountNumber());
        log.info("User id: {}", transactionCreatedEvent.getUserId());
        log.info("Amount: {}", transactionCreatedEvent.getAmount());
        log.info("Transaction Type: {}", transactionCreatedEvent.getTransactionType());
        log.info("Balance: {}", transactionCreatedEvent.getBalance());
        log.info("Status: {}", transactionCreatedEvent.getStatus());
        log.info("Description: {}", transactionCreatedEvent.getDescription());
        log.info("Created At: {}", transactionCreatedEvent.getCreatedAt());

        String id = generateEventUUID();
        ProducerRecord<String, TransactionCreatedEvent> record = new ProducerRecord<>(topicName, id, transactionCreatedEvent);
        record.headers().add("messageId", id.getBytes());

        SendResult<String, TransactionCreatedEvent> result = kafkaTemplate.send(record).get();

        log.info("Event is successfully published to: {}", result.getRecordMetadata().topic());

    }

    private String generateEventUUID(){
        return UUID.randomUUID().toString();
    }

}
