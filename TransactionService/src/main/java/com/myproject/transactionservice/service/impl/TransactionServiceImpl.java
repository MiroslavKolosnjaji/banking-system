package com.myproject.transactionservice.service.impl;

import com.myproject.transactionservice.dto.*;
import com.myproject.transactionservice.exception.service.TransactionNotFoundException;
import com.myproject.transactionservice.exception.service.strategy.UserNotFoundException;
import com.myproject.transactionservice.mapper.TransactionMapper;
import com.myproject.transactionservice.service.NotificationService;
import com.myproject.transactionservice.model.Transaction;
import com.myproject.transactionservice.repository.TransactionRepository;
import com.myproject.transactionservice.service.TransactionService;
import com.myproject.transactionservice.service.strategy.TransactionStrategyFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Miroslav Kološnjaji
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionStrategyFactory transactionStrategyFactory;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final NotificationService notificationService;

    @Override
    public TransactionDTO deposit(DepositDTO depositDTO) {

        TransactionDetailsDTO transactionDetailsDTO = transactionStrategyFactory.performTransaction(depositDTO);
        TransactionDTO savedDTO = save(populateTransactionDTO(transactionDetailsDTO, depositDTO));
        sendNotification(transactionDetailsDTO, savedDTO);

        return savedDTO;
    }

    @Override
    public TransactionDTO withdraw(WithdrawDTO withdrawDTO) {

        TransactionDetailsDTO transactionDetailsDTO = transactionStrategyFactory.performTransaction(withdrawDTO);
        TransactionDTO savedDTO = save(populateTransactionDTO(transactionDetailsDTO, withdrawDTO));
        sendNotification(transactionDetailsDTO, savedDTO);

        return savedDTO;
    }

    @Override
    public TransactionDTO transfer(TransferDTO transferDTO) {

        TransactionDetailsDTO transactionDetailsDTO = transactionStrategyFactory.performTransaction(transferDTO);
        TransactionDTO savedDTO = save(populateTransactionDTO(transactionDetailsDTO, transferDTO));
        sendNotification(transactionDetailsDTO, savedDTO);

        return savedDTO;
    }

    @Override
    public List<TransactionDTO> getAll() {
        return transactionRepository.findAll().stream().map(transactionMapper::transactionToTransactionDTO).toList();
    }

    @Override
    public TransactionDTO getById(Long id) throws TransactionNotFoundException {

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        return transactionMapper.transactionToTransactionDTO(transaction);
    }

    TransactionDTO save(TransactionDTO transactionDTO) {

        log.info("TransactionDTO userID: {}", transactionDTO.getUserId());

        Transaction savedTransaction = transactionRepository.save(transactionMapper.transactionDTOToTransaction(transactionDTO));
        return transactionMapper.transactionToTransactionDTO(savedTransaction);
    }

    private TransactionDTO populateTransactionDTO(TransactionDetailsDTO transactionDetailsDTO, BaseDTO baseDTO) {

        return TransactionDTO.builder()
                .userId(baseDTO.getUserId())
                .accountId(baseDTO.getAccountId())
                .transactionType(baseDTO.getTransactionType())
                .amount(baseDTO.getAmount())
                .status(transactionDetailsDTO.getStatus())
                .description(transactionDetailsDTO.getDescription())
                .currency(transactionDetailsDTO.getCurrency())
                .build();

    }

    private void sendNotification(TransactionDetailsDTO transactionDetailsDTO, TransactionDTO transactionDTO) {

        try {
            notificationService.sendNotification(transactionDetailsDTO, transactionDTO);
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
