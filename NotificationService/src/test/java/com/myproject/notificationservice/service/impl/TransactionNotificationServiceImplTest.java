package com.myproject.notificationservice.service.impl;

import com.myproject.notificationservice.dto.TransactionNotificationDTO;
import com.myproject.notificationservice.exception.service.NotificationNotFoundException;
import com.myproject.notificationservice.mapper.TransactionNotificationMapper;
import com.myproject.notificationservice.model.NotificationType;
import com.myproject.notificationservice.model.TransactionNotification;
import com.myproject.notificationservice.repository.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Miroslav Kološnjaji
 */
@ExtendWith(MockitoExtension.class)
class TransactionNotificationServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionNotificationMapper transactionNotificationMapper;

    @InjectMocks
    private TransactionNotificationServiceImpl transactionNotificationService;


    @DisplayName("Save Notification")
    @Test
    void testSave_whenValidDetailsProvided_returnsTransactionDTO() {

        String messageId = UUID.randomUUID().toString();
        TransactionNotification mockedNotification = mock(TransactionNotification.class);

        TransactionNotificationDTO transactionNotificationDTO =
                TransactionNotificationDTO.builder()
                        .transactionId(1L)
                        .userId(1L)
                        .accountNumber("12345****34")
                        .transactionType("DEPOSIT")
                        .status("SUCCESSFUL")
                        .amount(new BigDecimal("100"))
                        .balance(new BigDecimal("1238.99"))
                        .notificationType(NotificationType.SMS)
                        .createdAt(Instant.now())
                        .build();

        when(transactionNotificationMapper.transactionNotificationDTOToTransactionNotification(transactionNotificationDTO)).thenReturn(mockedNotification);
        when(transactionNotificationMapper.transactionNotificationToTransactionNotificationDTO(mockedNotification)).thenReturn(transactionNotificationDTO);
        when(transactionRepository.save(any(TransactionNotification.class))).thenReturn(mockedNotification);


        TransactionNotificationDTO savedNotification = transactionNotificationService.save(messageId, transactionNotificationDTO);

        assertNotNull(savedNotification, "Saved notification should not be null.");
        assertEquals(transactionNotificationDTO, savedNotification, "Saved notification doesn't match expected notification");

        verify(transactionNotificationMapper).transactionNotificationDTOToTransactionNotification(transactionNotificationDTO);
        verify(transactionNotificationMapper).transactionNotificationToTransactionNotificationDTO(mockedNotification);
        verify(transactionRepository).save(any(TransactionNotification.class));

    }

    @DisplayName("Get All Notifications")
    @Test
    void testGetAll_whenListIsPopulated_returnsPopulatedDTOList() {

        TransactionNotificationDTO mockedNotificationDTO = mock(TransactionNotificationDTO.class);
        List<TransactionNotification> notificationList = List.of(mock(TransactionNotification.class), mock(TransactionNotification.class), mock(TransactionNotification.class));

        when(transactionRepository.findAll()).thenReturn(notificationList);
        when(transactionNotificationMapper.transactionNotificationToTransactionNotificationDTO(any(TransactionNotification.class))).thenReturn(mockedNotificationDTO);

        List<TransactionNotificationDTO> dtoList = transactionNotificationService.getAll();

        assertNotNull(dtoList, "DTO list should not be null.");
        assertEquals(3, dtoList.size(), "List size should be 3.");

        verify(transactionRepository).findAll();
        verify(transactionNotificationMapper, times(3)).transactionNotificationToTransactionNotificationDTO(any(TransactionNotification.class));
    }

    @DisplayName("Get All Notifications from User")
    @Test
    void testGetAllUsersNotifications_whenValidIdProvided_returnsDTOList() throws NotificationNotFoundException {

        List<TransactionNotification> notificationList = List.of(mock(TransactionNotification.class), mock(TransactionNotification.class), mock(TransactionNotification.class));
        List<TransactionNotificationDTO> notificationDTOListList = List.of(mock(TransactionNotificationDTO.class), mock(TransactionNotificationDTO.class), mock(TransactionNotificationDTO.class));

        when(transactionRepository.findAllByUserId(anyLong())).thenReturn(Optional.of(notificationList));
        when(transactionNotificationMapper.toDTOList(notificationList)).thenReturn(notificationDTOListList);


        List<TransactionNotificationDTO> userNotificationList = transactionNotificationService.getAllUserNotifications(anyLong());

        assertNotNull(userNotificationList, "User notification list should not be null.");
        assertEquals(notificationDTOListList.size(), userNotificationList.size(), "List size should be same as expected list size.");

        verify(transactionRepository).findAllByUserId(anyLong());
        verify(transactionNotificationMapper).toDTOList(notificationList);

    }
}