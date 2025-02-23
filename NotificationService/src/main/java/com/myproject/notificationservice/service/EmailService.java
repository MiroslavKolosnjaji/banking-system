package com.myproject.notificationservice.service;

import com.myproject.notificationservice.dto.TransactionNotificationDTO;
import jakarta.mail.MessagingException;

/**
 * @author Miroslav Kološnjaji
 */
public interface EmailService {

    void sendEmail(String to, String subject, String body) throws MessagingException;
    void prepareEmail(Object object);
}

