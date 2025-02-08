package com.myproject.notificationservice.service.impl;

import com.myproject.notificationservice.dto.TransactionNotificationDTO;
import com.myproject.notificationservice.exception.service.NotificationNotFoundException;
import com.myproject.notificationservice.mapper.TransactionNotificationMapper;
import com.myproject.notificationservice.model.TransactionNotification;
import com.myproject.notificationservice.repository.TransactionRepository;
import com.myproject.notificationservice.service.TransactionNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Miroslav Kološnjaji
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionNotificationServiceImpl implements TransactionNotificationService {

    private final TransactionRepository transactionRepository;
    private final TransactionNotificationMapper transactionNotificationMapper;


    @Override
    public TransactionNotificationDTO save(String messageId, TransactionNotificationDTO transactionNotificationDTO) {

       TransactionNotification transactionNotification = transactionNotificationMapper.transactionNotificationDTOToTransactionNotification(transactionNotificationDTO);
       transactionNotification.setMessageId(messageId);

       TransactionNotification saved = transactionRepository.save(transactionNotification);

        log.info("TRANSACTION SAVED SUCCESSFULLY: {}", saved);
        log.info("SAVED TRANSACTION ID: {}", saved.getId());

        //call notification service to notify user about his transaction using one of sms services.

        return transactionNotificationMapper.transactionNotificationToTransactionNotificationDTO(saved);
    }

    @Override
    public List<TransactionNotificationDTO> getAll() {
        return transactionRepository.findAll().stream().map(transactionNotificationMapper::transactionNotificationToTransactionNotificationDTO).toList();
    }

    @Override
    public List<TransactionNotificationDTO> getAllUserNotifications(Long userId) throws NotificationNotFoundException {

        List<TransactionNotification> notificationList = transactionRepository.findAllByUserId(userId)
                .orElseThrow(() -> new NotificationNotFoundException("Notifications doesn't exists for this user"));

        return transactionNotificationMapper.toDTOList(notificationList);
    }

    public boolean messageExists(String messageId) {
        return transactionRepository.findByMessageId(messageId).isPresent();
    }
}
