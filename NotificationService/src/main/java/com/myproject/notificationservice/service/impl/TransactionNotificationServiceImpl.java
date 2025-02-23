package com.myproject.notificationservice.service.impl;

import com.myproject.notificationservice.dto.TransactionNotificationDTO;
import com.myproject.notificationservice.exception.service.NotificationNotFoundException;
import com.myproject.notificationservice.mapper.TransactionNotificationMapper;
import com.myproject.notificationservice.model.EmailStatus;
import com.myproject.notificationservice.model.TransactionNotification;
import com.myproject.notificationservice.repository.TransactionRepository;
import com.myproject.notificationservice.service.EmailService;
import com.myproject.notificationservice.service.TransactionNotificationService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Miroslav Kološnjaji
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionNotificationServiceImpl implements TransactionNotificationService {

    private final TransactionRepository transactionRepository;
    private final TransactionNotificationMapper transactionNotificationMapper;
    private final EmailService emailService;


    @Override
    public TransactionNotificationDTO save(String messageId, TransactionNotificationDTO transactionNotificationDTO) throws NotificationNotFoundException {

        TransactionNotification transactionNotification = transactionNotificationMapper.transactionNotificationDTOToTransactionNotification(transactionNotificationDTO);
        transactionNotification.setMessageId(messageId);
        transactionNotification.setEmailStatus(EmailStatus.PENDING);

        TransactionNotification saved = transactionRepository.save(transactionNotification);

        log.info("TRANSACTION SAVED SUCCESSFULLY: {}", saved);
        log.info("SAVED TRANSACTION ID: {}", saved.getId());

        emailService.prepareEmail(transactionNotificationDTO);

        transactionNotification.setEmailStatus(EmailStatus.DELIVERED);

        return updateEmailStatus(messageId, transactionNotification.getEmailStatus());
    }

    @Override
    public TransactionNotificationDTO updateEmailStatus(String messageId, EmailStatus emailStatus) throws NotificationNotFoundException {

        Optional<TransactionNotificationDTO> transactionNotificationDTO = getTransactionNotificationByMessageId(messageId);

        if(transactionNotificationDTO.isEmpty())
            throw new NotificationNotFoundException("Notification not found.");

        TransactionNotification transactionNotification = transactionNotificationMapper.transactionNotificationDTOToTransactionNotification(transactionNotificationDTO.get());

        transactionNotification.setEmailStatus(emailStatus);
        TransactionNotification updated = transactionRepository.save(transactionNotification);


        return transactionNotificationMapper.transactionNotificationToTransactionNotificationDTO(updated);
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

    @Override
    public TransactionNotificationDTO retryToSendEmail(String messageId) throws NotificationNotFoundException {

        Optional<TransactionNotificationDTO> transactionNotificationDTO = getTransactionNotificationByMessageId(messageId);

        if(transactionNotificationDTO.isEmpty())
            throw new NotificationNotFoundException("Notification not found.");

        emailService.prepareEmail(transactionNotificationDTO.get());

        return updateEmailStatus(messageId, EmailStatus.DELIVERED);
    }

    @Override
    public Optional<TransactionNotificationDTO> getTransactionNotificationByMessageId(String messageId){

        Optional<TransactionNotification> transactionNotification = transactionRepository.findByMessageId(messageId);

        return transactionNotification.map(transactionNotificationMapper::transactionNotificationToTransactionNotificationDTO);
    }
}
