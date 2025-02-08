package com.myproject.transactionservice.service.strategy;

import com.myproject.transactionservice.dto.BaseDTO;
import com.myproject.transactionservice.dto.TransactionDetailsDTO;
import com.myproject.transactionservice.exception.controller.EntityNotFoundException;
import com.myproject.transactionservice.exception.service.strategy.UserNotFoundException;
import com.myproject.transactionservice.model.Status;

import java.util.Currency;

/**
 * @author Miroslav Kološnjaji
 */
public interface TransactionStrategy {

    TransactionDetailsDTO performTransaction(BaseDTO baseDTO);

    default Status setStatus(TransactionDetailsDTO transactionDetailsDTO) {

        if (transactionDetailsDTO.getDescription() != null)
            transactionDetailsDTO.setStatus(Status.FAILED);

        return transactionDetailsDTO.getStatus();
    }

    default String setUpDescription(String description) {

        if (description == null)
            return "Transaction is successfully performed.";

        return description;
    }

    default String setCurrency(String actialCurrency, String requestedCurrency){
        return actialCurrency == null ? requestedCurrency : actialCurrency;
    }
}
