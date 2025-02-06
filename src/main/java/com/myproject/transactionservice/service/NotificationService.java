package com.myproject.transactionservice.service;

import com.myproject.transactionservice.dto.TransactionDTO;
import com.myproject.transactionservice.dto.TransactionDetailsDTO;

import java.util.concurrent.ExecutionException;

/**
 * @author Miroslav Kološnjaji
 */

public interface NotificationService {

    void sendNotification(TransactionDetailsDTO transactionDetailsDTO, TransactionDTO transactionDTO) throws ExecutionException, InterruptedException;
}
