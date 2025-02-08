package com.myproject.transactionservice.service.strategy;

import com.myproject.transactionservice.client.account.AccountClient;
import com.myproject.transactionservice.client.dto.WithdrawRequestDTO;
import com.myproject.transactionservice.client.response.AccountResponse;
import com.myproject.transactionservice.client.user.UserClient;
import com.myproject.transactionservice.dto.BaseDTO;
import com.myproject.transactionservice.dto.TransactionDetailsDTO;
import com.myproject.transactionservice.dto.WithdrawDTO;
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
public class WithdrawStrategy implements TransactionStrategy {

    private final AccountMapper accountMapper;
    private final AccountClient accountClient;
    private final UserClient userClient;

    @Override
    public TransactionDetailsDTO performTransaction(BaseDTO baseDTO){

        WithdrawDTO withdrawDTO = (WithdrawDTO) baseDTO;

        userClient.isUserExists(withdrawDTO.getUserId());

        WithdrawRequestDTO withdrawRequestDTO = accountMapper.withdrawDTOToWithdrawRequestDTO(withdrawDTO);
        AccountResponse accountResponse = accountClient.withdraw(withdrawDTO.getAccountId(), withdrawRequestDTO);
        TransactionDetailsDTO transactionDetailsDTO = accountMapper.accountResponseToTransactionDetailsDTO(accountResponse);

        transactionDetailsDTO.setStatus(setStatus(transactionDetailsDTO));
        transactionDetailsDTO.setDescription(setUpDescription(transactionDetailsDTO.getDescription()));

        return transactionDetailsDTO;
    }
}
