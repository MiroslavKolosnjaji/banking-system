package com.myproject.notificationservice.mapper;

import com.myproject.notificationservice.dto.TransactionNotificationDTO;
import com.myproject.notificationservice.model.TransactionNotification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * @author Miroslav Kološnjaji
 */
@Mapper
public interface TransactionNotificationMapper {

    TransactionNotificationDTO transactionNotificationToTransactionNotificationDTO(TransactionNotification transactionNotification);

    @Mapping(target = "messageId", ignore = true)
    TransactionNotification transactionNotificationDTOToTransactionNotification(TransactionNotificationDTO transactionNotificationDTO);

    List<TransactionNotificationDTO> toDTOList(List<TransactionNotification> transactionNotificationList);

}
