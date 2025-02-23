package com.myproject.notificationservice.messaging.handler;

import com.myproject.core.transaction.event.TransactionCreatedEvent;
import com.myproject.notificationservice.dto.TransactionNotificationDTO;
import com.myproject.notificationservice.exception.service.NotificationNotFoundException;
import com.myproject.notificationservice.model.EmailStatus;
import com.myproject.notificationservice.service.TransactionNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Miroslav Kološnjaji
 */
@ExtendWith(MockitoExtension.class)
class TransactionCreatedEventHandlerTest {

    @Mock
    private TransactionNotificationService transactionNotificationService;

    @InjectMocks
    private TransactionCreatedEventHandler transactionCreatedEventHandler;

    private TransactionCreatedEvent transactionCreatedEvent;
    private TransactionNotificationDTO transactionNotificationDTO;

    @BeforeEach
    void setUp() {

        Instant date = Instant.now();

        transactionCreatedEvent = TransactionCreatedEvent.builder()
                .userId(1L)
                .transactionId(1L)
                .email("test@example.com")
                .accountNumber("1239135430189545")
                .transactionType("WITHDRAWAL")
                .amount(new BigDecimal("100"))
                .balance(new BigDecimal("355"))
                .currency("USD")
                .description("Transaction successfully performed.")
                .status("Successful")
                .createdAt(date)
                .build();

        transactionNotificationDTO = TransactionNotificationDTO.builder()
                .userId(1L)
                .transactionId(1L)
                .email("test@example.com")
                .accountNumber("1239135430189545")
                .transactionType("WITHDRAWAL")
                .amount(new BigDecimal("100"))
                .balance(new BigDecimal("355"))
                .currency("USD")
                .description("Transaction successfully performed.")
                .status("Successful")
                .createdAt(date)
                .build();

    }

    @DisplayName("New Event")
    @Test
    void testHandleEvent_whenEventDoesntExistsInDB_thenCorrect() throws NotificationNotFoundException {


        String messageId = "testMessageId";

        when(transactionNotificationService.getTransactionNotificationByMessageId(messageId)).thenReturn(Optional.empty());
        when(transactionNotificationService.save(messageId, transactionNotificationDTO)).thenReturn(transactionNotificationDTO);


        Executable executable = () -> transactionCreatedEventHandler.handleEvent(transactionCreatedEvent, messageId);


        assertDoesNotThrow(executable, "Method should not throw an exception.");

        verify(transactionNotificationService).getTransactionNotificationByMessageId(messageId);
        verify(transactionNotificationService).save(messageId, transactionNotificationDTO);
    }


    @DisplayName("Event Not Processed")
    @Test
    void testHandleEvent_whenEventAlreadyExistsButNotProcessed_thenCorrect() throws NotificationNotFoundException {

        String messageId = "testMessageId";
        transactionNotificationDTO.setEmailStatus(EmailStatus.PENDING);

        when(transactionNotificationService.getTransactionNotificationByMessageId(messageId)).thenReturn(Optional.of(transactionNotificationDTO));
        when(transactionNotificationService.retryToSendEmail(messageId)).thenReturn(transactionNotificationDTO);


        Executable executable = () -> transactionCreatedEventHandler.handleEvent(transactionCreatedEvent, messageId);


        assertDoesNotThrow(executable, "Method should not throw an exception.");

        verify(transactionNotificationService).getTransactionNotificationByMessageId(messageId);
        verify(transactionNotificationService).retryToSendEmail(messageId);
    }

    @DisplayName("Event Processed")
    @Test
    void testHandleEvent_whenEventAlreadyExistsAndProcessed_thenCorrect() throws NotificationNotFoundException {

        String messageId = "testMessageId";
        transactionNotificationDTO.setEmailStatus(EmailStatus.DELIVERED);

        when(transactionNotificationService.getTransactionNotificationByMessageId(messageId)).thenReturn(Optional.of(transactionNotificationDTO));


        Executable executable = () -> transactionCreatedEventHandler.handleEvent(transactionCreatedEvent, messageId);


        assertDoesNotThrow(executable, "Method should not throw an exception.");

        verify(transactionNotificationService).getTransactionNotificationByMessageId(messageId);
    }
}