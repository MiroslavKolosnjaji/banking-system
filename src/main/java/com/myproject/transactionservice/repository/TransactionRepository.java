package com.myproject.transactionservice.repository;

import com.myproject.transactionservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Miroslav Kološnjaji
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
