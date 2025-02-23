package com.myproject.notificationservice.service.impl;

import com.myproject.notificationservice.dto.TransactionNotificationDTO;
import com.myproject.notificationservice.exception.service.NotificationNotFoundException;
import com.myproject.notificationservice.mapper.TransactionNotificationMapper;
import com.myproject.notificationservice.model.EmailStatus;
import com.myproject.notificationservice.model.TransactionNotification;
import com.myproject.notificationservice.repository.TransactionRepository;
import com.myproject.notificationservice.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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

    @Mock
    private EmailService emailService;

    @Spy
    @InjectMocks
    private TransactionNotificationServiceImpl transactionNotificationService;

    private TransactionNotificationDTO transactionNotificationDTO;

    Instant time = Instant.now();

    @BeforeEach
    void setUp() {
        transactionNotificationDTO = TransactionNotificationDTO.builder()
                .transactionId(1L)
                .userId(1L)
                .email("test@example.com")
                .accountNumber("12345****34")
                .transactionType("DEPOSIT")
                .status("SUCCESSFUL")
                .amount(new BigDecimal("100"))
                .balance(new BigDecimal("1238.99"))
                .createdAt(time)
                .build();
    }

    @DisplayName("Save Notification")
    @Test
    void testSave_whenValidDetailsProvided_returnsTransactionDTO() throws Exception {


        String messageId = UUID.randomUUID().toString();
        TransactionNotification transactionNotification = mock(TransactionNotification.class);

        when(transactionNotificationMapper.transactionNotificationDTOToTransactionNotification(transactionNotificationDTO)).thenReturn(transactionNotification);
        when(transactionRepository.save(any(TransactionNotification.class))).thenReturn(transactionNotification);
        doNothing().when(emailService).prepareEmail(transactionNotificationDTO);
        doReturn(transactionNotificationDTO).when(transactionNotificationService).updateEmailStatus(messageId, transactionNotificationDTO.getEmailStatus());

        TransactionNotificationDTO savedNotification = transactionNotificationService.save(messageId, transactionNotificationDTO);

        assertNotNull(savedNotification, "Saved notification should not be null.");
        assertEquals(transactionNotificationDTO, savedNotification, "Saved notification doesn't match expected notification");

        verify(transactionNotificationMapper).transactionNotificationDTOToTransactionNotification(transactionNotificationDTO);
        verify(transactionRepository).save(any(TransactionNotification.class));
        verify(emailService).prepareEmail(transactionNotificationDTO);
        verify(transactionNotificationService).updateEmailStatus(messageId, transactionNotificationDTO.getEmailStatus());
    }

    @DisplayName("Update Email Status")
    @Test
    void testValidateEmailStatus_whenValidStatusProvided_thenReturnTransactionNotificationDTO() throws Exception {

        TransactionNotification transactionNotification = mock(TransactionNotification.class);

        doReturn(Optional.of(transactionNotificationDTO)).when(transactionNotificationService).getTransactionNotificationByMessageId(anyString());
        when(transactionNotificationMapper.transactionNotificationDTOToTransactionNotification(transactionNotificationDTO)).thenReturn(transactionNotification);
        when(transactionRepository.save(any(TransactionNotification.class))).thenReturn(transactionNotification);
        when(transactionNotificationMapper.transactionNotificationToTransactionNotificationDTO(transactionNotification)).thenReturn(transactionNotificationDTO);

        TransactionNotificationDTO updatedNotification = transactionNotificationService.updateEmailStatus("testMessage", transactionNotification.getEmailStatus());

        assertNotNull(updatedNotification, "Updated transaction should not be null.");
        assertEquals(transactionNotificationDTO.getEmailStatus(), updatedNotification.getEmailStatus(), "Email status should be equal.");

        verify(transactionNotificationService).getTransactionNotificationByMessageId(anyString());
        verify(transactionNotificationMapper).transactionNotificationDTOToTransactionNotification(transactionNotificationDTO);
        verify(transactionRepository).save(any(TransactionNotification.class));
        verify(transactionNotificationMapper).transactionNotificationToTransactionNotificationDTO(transactionNotification);
    }

    @DisplayName("Update Email Status FAILED - Notification Not Found")
    @Test
    void testUpdateEmailStatus_whenNotificationNotFound_throwsNotificationNotFoundException() {

        doReturn(Optional.empty()).when(transactionNotificationService).getTransactionNotificationByMessageId(anyString());

        Executable executable = () -> transactionNotificationService.updateEmailStatus(anyString(), any(EmailStatus.class));

        assertThrows(NotificationNotFoundException.class, executable, "Exception mismatch. Expected NotificationNotFound exception.");
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

    @DisplayName("Retry to Send Email")
    @Test
    void testRetryToSendEmail_whenValidMessageIdProvided_thenReturnTransactionNotificationDTO() throws Exception {

        doReturn(Optional.of(transactionNotificationDTO)).when(transactionNotificationService).getTransactionNotificationByMessageId(anyString());
        doNothing().when(emailService).prepareEmail(transactionNotificationDTO);
        doReturn(transactionNotificationDTO).when(transactionNotificationService).updateEmailStatus(anyString(), any(EmailStatus.class));

        TransactionNotificationDTO updatedDTO = transactionNotificationService.retryToSendEmail("testMessage");

        assertNotNull(updatedDTO, "Updated TransactionNotificationDTO should not be null.");
        assertEquals(transactionNotificationDTO.getEmailStatus(), updatedDTO.getEmailStatus(), "Email status mismatch.");

        verify(transactionNotificationService).getTransactionNotificationByMessageId(anyString());
        verify(emailService).prepareEmail(transactionNotificationDTO);
        verify(transactionNotificationService).updateEmailStatus(anyString(), any(EmailStatus.class));
    }

    @DisplayName("Retry to Send Email FAILED - Notification Not Found")
    @Test
    void testRetryToSendEmail_whenNotificationNotFound_throwsNotificationNotFoundException() {

        doReturn(Optional.empty()).when(transactionNotificationService).getTransactionNotificationByMessageId(anyString());

        Executable executable = () -> transactionNotificationService.retryToSendEmail(anyString());

        assertThrows(NotificationNotFoundException.class, executable, "Exception mismatch. Expected NotificationNotFound exception.");

    }

    @DisplayName("Get Transaction Notification By Message ID")
    @Test
    void testGetTransactionNotificationByMessageId_whenValidMessageIdProvided_returnsOptionalOfTransactionDTO() {

        TransactionNotification transactionNotification = mock(TransactionNotification.class);

        when(transactionRepository.findByMessageId(anyString())).thenReturn(Optional.of(transactionNotification));
        when(transactionNotificationMapper.transactionNotificationToTransactionNotificationDTO(transactionNotification)).thenReturn(transactionNotificationDTO);

        Optional<TransactionNotificationDTO> foundTransactionNotificationDTO = transactionNotificationService.getTransactionNotificationByMessageId(anyString());

        assertFalse(foundTransactionNotificationDTO.isEmpty(), "TransactionNotificationDTO (optional) should not be empty");


        verify(transactionRepository).findByMessageId(anyString());
        verify(transactionNotificationMapper).transactionNotificationToTransactionNotificationDTO(transactionNotification);
    }
}