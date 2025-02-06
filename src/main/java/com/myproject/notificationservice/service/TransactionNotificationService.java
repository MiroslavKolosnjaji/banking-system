package com.myproject.notificationservice.service;

import com.myproject.notificationservice.dto.TransactionNotificationDTO;
import com.myproject.notificationservice.exception.service.NotificationNotFoundException;

import java.util.List;

/**
 * @author Miroslav Kološnjaji
 */
public interface TransactionNotificationService {

    TransactionNotificationDTO save(String messageId, TransactionNotificationDTO transactionNotificationDTO);
    List<TransactionNotificationDTO> getAll();
    List<TransactionNotificationDTO> getAllUserNotifications(Long userId) throws NotificationNotFoundException;
    boolean messageExists(String messageId);
}
