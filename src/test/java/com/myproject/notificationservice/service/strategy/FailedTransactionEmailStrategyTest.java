package com.myproject.notificationservice.service.strategy;

import com.myproject.notificationservice.dto.TransactionNotificationDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

/**
 * @author Miroslav Kološnjaji
 */
@ExtendWith(MockitoExtension.class)
class FailedTransactionEmailStrategyTest {


    @Mock
    private TemplateEngine templateEngine;

    @Spy
    @InjectMocks
    private FailedTransactionEmailStrategy failedTransactionEmailStrategy;

    @DisplayName("Prepare Email Template")
    @Test
    void testPrepareEmailTemplate_whenValidInputProvided_thenReturnString() {

        Instant date = Instant.now();
        String template = "testTemplate";

        LocalDateTime localDateTime = date.atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss");

        Context context = new Context();
        TransactionNotificationDTO transactionNotificationDTO = TransactionNotificationDTO.builder()
                .userId(1L)
                .transactionId(1L)
                .accountNumber("10473982y0923875093187")
                .amount(new BigDecimal("12345"))
                .balance(new BigDecimal("3334455"))
                .currency("RSD")
                .transactionType("DEPOSIT")
                .email("test@example.com")
                .description("Test")
                .status("SUCCESSFUL")
                .createdAt(date)
                .build();

        Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("transactionType", transactionNotificationDTO.getTransactionType());
        contextMap.put("details", transactionNotificationDTO.getDescription());
        contextMap.put("header", transactionNotificationDTO.getStatus());
        contextMap.put("date", localDateTime.format(formatter));

        doReturn(context).when(failedTransactionEmailStrategy).getContext(contextMap);
        when(templateEngine.process(failedTransactionEmailStrategy.EMAIL_TEMPLATE, context)).thenReturn(template);

        Map<String, String> result = failedTransactionEmailStrategy.prepareEmailTemplate(transactionNotificationDTO);

        assertNotNull(result, "Result should not be null.");

        verify(failedTransactionEmailStrategy).getContext(contextMap);
        verify(templateEngine).process(failedTransactionEmailStrategy.EMAIL_TEMPLATE, context);
    }

    @DisplayName("Prepare Email Template FAILED")
    @Test
    void testPrepareEmailTemplate_whenInvalidInputProvided_thenThrowsIllegalArgumentException() {

        String test = "test";

        Executable executable = () -> failedTransactionEmailStrategy.prepareEmailTemplate(test);

        assertThrows(IllegalArgumentException.class, executable, "Exception mismatch. Expecting IllegalArgumentException.");
    }

    @DisplayName("Supports class")
    @Test
    void testSupports_whenValidInputProvided_thenReturnTrue() {

        TransactionNotificationDTO transactionNotificationDTO = new TransactionNotificationDTO();
        transactionNotificationDTO.setStatus("FAILED");

        boolean supports = failedTransactionEmailStrategy.supports(transactionNotificationDTO.getClass(), transactionNotificationDTO);

        assertTrue(supports, "This method should support TransactionNotificationDTO class.");
    }

}