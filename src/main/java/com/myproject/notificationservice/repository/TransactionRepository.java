package com.myproject.notificationservice.repository;

import com.myproject.notificationservice.model.TransactionNotification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Miroslav Kološnjaji
 */
public interface TransactionRepository extends MongoRepository<TransactionNotification, String> {

    Optional<TransactionNotification> findByMessageId(String messageId);
    Optional<List<TransactionNotification>> findAllByUserId(Long userId);
}
