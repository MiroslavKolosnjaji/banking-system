package com.myproject.transactionservice.bootstrap;

import com.myproject.transactionservice.dto.TransactionDTO;
import com.myproject.transactionservice.model.Transaction;
import com.myproject.transactionservice.model.TransactionType;
import com.myproject.transactionservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author Miroslav Kološnjaji
 */
@Component
@RequiredArgsConstructor
@Profile("dev")
public class BootstrapData implements CommandLineRunner {

    private final TransactionRepository transactionRepository;

    @Override
    public void run(String... args) throws Exception {
//        loadTransactionData();
    }

    private void loadTransactionData() {
        Transaction transaction = Transaction.builder()
                .userId(1L)
                .accountId(1L)
                .transactionType(TransactionType.DEPOSIT)
                .amount(new BigDecimal("100"))
                .build();

        transactionRepository.save(transaction);
    }
}
