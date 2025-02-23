package com.myproject.notificationservice.service.impl;

import com.myproject.notificationservice.dto.TransactionNotificationDTO;
import com.myproject.notificationservice.service.strategy.EmailStrategyFactory;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Miroslav Kološnjaji
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    EmailStrategyFactory emailStrategyFactory;

    @Spy
    @InjectMocks
    private EmailServiceImpl emailService;

    @DisplayName("Send Email")
    @Test
    void testSendEmail_whenValidEmailProvided_thenCorrect() {

        String to = "test@example.com";
        String subject = "Test";
        String body = "HTML BODY";

        MimeMessage mimeMessage  = new MimeMessage((Session) null);
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        Executable executable = () -> emailService.sendEmail(to, subject, body);

        assertDoesNotThrow(executable, "Method should not throw MessageException.");

        verify(javaMailSender).createMimeMessage();
        verify(javaMailSender).send(mimeMessage);
    }

    @DisplayName("Prepare Email")
    @Test
    void testPrepareEmail_whenValidDetailsProvided_thenCorrect() throws MessagingException {

        Map<String, String> map = new HashMap<>();
        map.put("email", "test@example.com");
        map.put("subject", "test subject");
        map.put("htmlBody", "HTML BODY");

        TransactionNotificationDTO transactionNotificationDTO = new TransactionNotificationDTO();
        transactionNotificationDTO.setEmail("test@xample.com");
        when(emailStrategyFactory.prepareEmailTemplate(transactionNotificationDTO)).thenReturn(map);
        doNothing().when(emailService).sendEmail(map.get("email"), map.get("subject"), map.get("htmlBody"));

        Executable executable = () -> emailService.prepareEmail(transactionNotificationDTO);

        assertDoesNotThrow(executable, "Method should not throw MessageException");

        verify(emailStrategyFactory).prepareEmailTemplate(transactionNotificationDTO);
        verify(emailService).sendEmail(map.get("email"), map.get("subject"), map.get("htmlBody"));

    }

    @DisplayName("Prepare Email FAILED")
    @Test
    void testPrepareEmail_whenInvalidDetailsProvided_thenThrowMessageException() throws MessagingException {

        Map<String, String> map = new HashMap<>();
        map.put("email", "test@example.com");
        map.put("subject", "test subject");
        map.put("htmlBody", "HTML BODY");


        TransactionNotificationDTO transactionNotificationDTO = new TransactionNotificationDTO();
        transactionNotificationDTO.setEmail("test@xample.com");
        when(emailStrategyFactory.prepareEmailTemplate(transactionNotificationDTO)).thenReturn(map);
        doThrow(MessagingException.class).when(emailService).sendEmail(map.get("email"), map.get("subject"), map.get("htmlBody"));

        Executable executable = () -> emailService.prepareEmail(transactionNotificationDTO);

        assertDoesNotThrow(executable, "Method should not throw MessageException");

        verify(emailStrategyFactory).prepareEmailTemplate(transactionNotificationDTO);
        verify(emailService).sendEmail(map.get("email"), map.get("subject"), map.get("htmlBody"));
    }
}