package com.myproject.transactionservice.service.strategy;

import com.myproject.transactionservice.dto.BaseDTO;
import com.myproject.transactionservice.dto.TransactionDetailsDTO;
import com.myproject.transactionservice.exception.controller.EntityNotFoundException;
import com.myproject.transactionservice.exception.service.strategy.UserNotFoundException;
import com.myproject.transactionservice.model.TransactionType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Miroslav Kološnjaji
 */
@Service
public class TransactionStrategyFactory {

    private final DepositStrategy depositStrategy;
    private final WithdrawStrategy withdrawStrategy;
    private final TransferStrategy transferStrategy;
    private Map<TransactionType, TransactionStrategy> strategyMap;

    public TransactionStrategyFactory(TransferStrategy transferStrategy, WithdrawStrategy withdrawStrategy, DepositStrategy depositStrategy) {
        this.transferStrategy = transferStrategy;
        this.withdrawStrategy = withdrawStrategy;
        this.depositStrategy = depositStrategy;
        populateMap();
    }

    public TransactionDetailsDTO performTransaction(BaseDTO baseDTO) {

        Optional<TransactionStrategy> transferStrategy = Optional.ofNullable(strategyMap.get(baseDTO.getTransactionType()));

        if (transferStrategy.isEmpty())
            throw new IllegalArgumentException("Invalid transaction type");

        return transferStrategy.get().performTransaction(baseDTO);
    }


    private void populateMap() {
        strategyMap = new HashMap<>();
        strategyMap.put(TransactionType.DEPOSIT, depositStrategy);
        strategyMap.put(TransactionType.WITHDRAWAL, withdrawStrategy);
        strategyMap.put(TransactionType.TRANSFER, transferStrategy);
    }
}
