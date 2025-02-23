package com.myproject.notificationservice.service.impl;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import com.myproject.notificationservice.dto.TransactionNotificationDTO;
import com.myproject.notificationservice.service.TransactionNotificationService;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.*;
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

    private static GreenMail greenMail;

    @BeforeAll
    static void beforeAll() {
        greenMail = new GreenMail(ServerSetup.SMTP);
        greenMail.start();

        greenMail.setUser("testUser", "testPassword");

        System.out.println("GreenMail started on port: " + greenMail.getSmtp().getPort());
    }


    @DynamicPropertySource
    private static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("email.host", () -> "localhost");
        registry.add("email.port", () -> greenMail.getSmtp().getPort());
        registry.add("email.username", () -> "testUser");
        registry.add("email.password", () -> "testPassword");
    }

    @Autowired
    private TransactionNotificationService transactionNotificationService;


    @DisplayName("Save Notification")
    @Order(1)
    @Test
    void testSaveNotification_whenValidDetailsProvided_thenCorrect() throws Exception {

        String messageId = UUID.randomUUID().toString();
        TransactionNotificationDTO transactionNotificationDTO = TransactionNotificationDTO.builder()
                .transactionId(1L)
                .userId(1L)
                .email("test@example.com")
                .accountNumber("12345****34")
                .transactionType("DEPOSIT")
                .status("SUCCESS")
                .amount(new BigDecimal("100"))
                .balance(new BigDecimal("1238.99"))
                .currency("RSD")
                .description("TEST")
                .createdAt(Instant.now())
                .build();


        TransactionNotificationDTO saved = transactionNotificationService.save(messageId, transactionNotificationDTO);

        assertNotNull(saved, "Saved transaction should not be null");
        assertNotNull(saved.getId(), "Id should not be null.");

        greenMail.waitForIncomingEmail(1);

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

        assertEquals(1, receivedMessages.length, "Expected one email to be sent.");


        MimeMessage message = receivedMessages[0];
        assertEquals("test@example.com", message.getAllRecipients()[0].toString());
        assertEquals("DEPOSIT", message.getSubject());
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