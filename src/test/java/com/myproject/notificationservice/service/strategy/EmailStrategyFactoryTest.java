package com.myproject.notificationservice.service.strategy;

import com.myproject.notificationservice.dto.TransactionNotificationDTO;
import com.myproject.notificationservice.model.EmailStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.thymeleaf.TemplateEngine;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.*;

/**
 * @author Miroslav Kološnjaji
 */
@ExtendWith(MockitoExtension.class)
class EmailStrategyFactoryTest {

    @Mock
    private SuccessfulTransactionEmailStrategy successfulTransactionEmailStrategy;

    @Mock
    private FailedTransactionEmailStrategy failedTransactionEmailStrategy;

    @InjectMocks
    private EmailStrategyFactory emailStrategyFactory;

    private TransactionNotificationDTO transactionNotificationDTO;

    @BeforeEach
    void setUp() {
        Map<String, BaseEmailStrategy> strategyMap = new HashMap<>();
        strategyMap.put("SUCCESSFUL", successfulTransactionEmailStrategy);
        strategyMap.put("FAILED", failedTransactionEmailStrategy);

        ReflectionTestUtils.setField(emailStrategyFactory, "strategies", strategyMap);


        transactionNotificationDTO = TransactionNotificationDTO.builder()
                .id("123asdf")
                .userId(1L)
                .transactionId(1L)
                .transactionType("DEPOSIT")
                .accountNumber("14973825692356209487101")
                .balance(new BigDecimal("123145"))
                .amount(new BigDecimal("3423"))
                .currency("RSD")
                .email("test@Example.com")
                .emailStatus(EmailStatus.PENDING)
                .status("SUCCESSFUL")
                .description("Transaction successfully performed.")
                .createdAt(Instant.now())
                .build();
    }

    @DisplayName("Prepare Email Template")
    @Test
    void testPrepareEmailTemplate_whenValidInputProvided_returnString() {


        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("email", "testEmail@example.com");
        expectedMap.put("subject", "Test");
        expectedMap.put("htmlBOdy", "TEST BODY");

        when(successfulTransactionEmailStrategy.supports(transactionNotificationDTO.getClass(), transactionNotificationDTO)).thenReturn(true);
        when(successfulTransactionEmailStrategy.prepareEmailTemplate(transactionNotificationDTO)).thenReturn(expectedMap);

        Map<String, String> result = emailStrategyFactory.prepareEmailTemplate(transactionNotificationDTO);

        assertNotNull(result);
        assertEquals(expectedMap, result);

        verify(successfulTransactionEmailStrategy).supports(transactionNotificationDTO.getClass(), transactionNotificationDTO);
        verify(successfulTransactionEmailStrategy).prepareEmailTemplate(transactionNotificationDTO);
    }


    @DisplayName("Prepare Email Template - Failed")
    @Test
    void testPrepareEmailTemplate_whenStrategyNotFound_thenThrowIllegalArgumentException() {

        when(successfulTransactionEmailStrategy.supports(transactionNotificationDTO.getClass(), transactionNotificationDTO)).thenReturn(false);

        Executable executable = () -> emailStrategyFactory.prepareEmailTemplate(transactionNotificationDTO);

        assertThrows(IllegalArgumentException.class, executable, "Exception mismatch. Expecting IllegalArgumentException.");

        verify(successfulTransactionEmailStrategy).supports(transactionNotificationDTO.getClass(), transactionNotificationDTO);
    }
}