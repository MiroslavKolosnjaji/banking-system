package com.myproject.transactionservice.service;

import com.myproject.transactionservice.dto.DepositDTO;
import com.myproject.transactionservice.dto.TransactionDTO;
import com.myproject.transactionservice.dto.TransferDTO;
import com.myproject.transactionservice.dto.WithdrawDTO;
import com.myproject.transactionservice.exception.service.TransactionNotFoundException;
import com.myproject.transactionservice.exception.service.strategy.UserNotFoundException;

import java.util.List;

/**
 * @author Miroslav Kološnjaji
 */
public interface TransactionService {

    TransactionDTO deposit(DepositDTO depositDTO);

    TransactionDTO withdraw(WithdrawDTO withdrawDTO);

    TransactionDTO transfer(TransferDTO transferDTO);

    List<TransactionDTO> getAll();

    TransactionDTO getById(Long id) throws TransactionNotFoundException;


}
