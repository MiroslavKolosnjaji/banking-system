package com.myproject.transactionservice.service.strategy;

import com.myproject.transactionservice.client.account.AccountClient;
import com.myproject.transactionservice.client.dto.DepositRequestDTO;
import com.myproject.transactionservice.client.response.AccountResponse;
import com.myproject.transactionservice.client.user.UserClient;
import com.myproject.transactionservice.dto.DepositDTO;
import com.myproject.transactionservice.dto.BaseDTO;
import com.myproject.transactionservice.dto.TransactionDetailsDTO;
import com.myproject.transactionservice.exception.controller.EntityNotFoundException;
import com.myproject.transactionservice.exception.service.strategy.UserNotFoundException;
import com.myproject.transactionservice.mapper.AccountMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * @author Miroslav Kološnjaji
 */
@Data
@Service
@RequiredArgsConstructor
public class DepositStrategy implements TransactionStrategy {

    private final AccountMapper accountMapper;
    private final AccountClient accountClient;
    private final UserClient userClient;

    @Override
    public TransactionDetailsDTO performTransaction(BaseDTO baseDTO) {

        DepositDTO depositDTO = (DepositDTO) baseDTO;

        userClient.isUserExists(depositDTO.getUserId());

        DepositRequestDTO depositRequest = accountMapper.depositDTOToDepositRequestDTO(depositDTO);
        AccountResponse accountResponse = accountClient.deposit(depositDTO.getAccountId(), depositRequest);
        TransactionDetailsDTO transactionDetailsDTO = accountMapper.accountResponseToTransactionDetailsDTO(accountResponse);

        transactionDetailsDTO.setStatus(setStatus(transactionDetailsDTO));
        transactionDetailsDTO.setDescription(setUpDescription(transactionDetailsDTO.getDescription()));

        return transactionDetailsDTO;
    }
}
