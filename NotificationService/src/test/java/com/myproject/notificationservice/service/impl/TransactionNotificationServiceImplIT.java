package com.myproject.notificationservice.service.impl;

import com.myproject.notificationservice.dto.TransactionNotificationDTO;
import com.myproject.notificationservice.model.NotificationType;
import com.myproject.notificationservice.service.TransactionNotificationService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.DisabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Miroslav Kološnjaji
 */
@Testcontainers
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransactionNotificationServiceImplIT {

    @Container
    @ServiceConnection
    private static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @Container
    @ServiceConnection
    private static MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:latest"));

    @Autowired
    private TransactionNotificationService transactionNotificationService;


    @DisplayName("Save Notification")
    @Order(1)
    @Test
    void testSaveNotification_whenValidDetailsProvided_thenCorrect() {

        String messageId = UUID.randomUUID().toString();
        TransactionNotificationDTO transactionNotificationDTO = TransactionNotificationDTO.builder()
                .transactionId(1L)
                .userId(1L)
                .accountNumber("12345****34")
                .transactionType("DEPOSIT")
                .status("SUCCESSFUL")
                .amount(new BigDecimal("100"))
                .balance(new BigDecimal("1238.99"))
                .notificationType(NotificationType.SMS)
                .createdAt(Instant.now())
                .build();


        TransactionNotificationDTO saved = transactionNotificationService.save(messageId, transactionNotificationDTO);

        assertNotNull(saved, "Saved transaction should not be null");
        assertNotNull(saved.getId(), "Id should not be null.");
    }

    @DisplayName("Get all Notifications")
    @Order(2)
    @Test
    void testGetAll_whenListIsPopulated_returnsDTOList() {

        List<TransactionNotificationDTO> list = transactionNotificationService.getAll();

        assertNotNull(list, "List should not be null.");
        assertFalse(list.isEmpty());
    }
}