package com.myproject.notificationservice.service;

import com.myproject.notificationservice.dto.TransactionNotificationDTO;
import com.myproject.notificationservice.exception.service.NotificationNotFoundException;
import com.myproject.notificationservice.model.EmailStatus;
import com.myproject.notificationservice.model.TransactionNotification;

import java.util.List;
import java.util.Optional;

/**
 * @author Miroslav Kološnjaji
 */
public interface TransactionNotificationService {

    TransactionNotificationDTO save(String messageId, TransactionNotificationDTO transactionNotificationDTO) throws NotificationNotFoundException;
    TransactionNotificationDTO updateEmailStatus(String messageId, EmailStatus emailStatus) throws NotificationNotFoundException;
    List<TransactionNotificationDTO> getAll();
    List<TransactionNotificationDTO> getAllUserNotifications(Long userId) throws NotificationNotFoundException;
    boolean messageExists(String messageId);
    TransactionNotificationDTO retryToSendEmail(String messageId) throws NotificationNotFoundException;
    Optional<TransactionNotificationDTO> getTransactionNotificationByMessageId(String messageId);
}
