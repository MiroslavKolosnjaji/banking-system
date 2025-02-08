package com.myproject.transactionservice.service.strategy;

import com.myproject.transactionservice.client.account.AccountClient;
import com.myproject.transactionservice.client.dto.TransferRequestDTO;
import com.myproject.transactionservice.client.response.AccountResponse;
import com.myproject.transactionservice.client.user.UserClient;
import com.myproject.transactionservice.dto.BaseDTO;
import com.myproject.transactionservice.dto.TransactionDetailsDTO;
import com.myproject.transactionservice.dto.TransferDTO;
import com.myproject.transactionservice.exception.controller.EntityNotFoundException;
import com.myproject.transactionservice.exception.service.strategy.UserNotFoundException;
import com.myproject.transactionservice.mapper.AccountMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

/**
 * @author Miroslav Kološnjaji
 */
@Data
@Service
@AllArgsConstructor
public class TransferStrategy implements TransactionStrategy {

    private final AccountMapper accountMapper;
    private final AccountClient accountClient;
    private final UserClient userClient;

    @Override
    public TransactionDetailsDTO performTransaction(BaseDTO baseDTO) {

        TransferDTO transferDTO = (TransferDTO) baseDTO;

        userClient.isUserExists(transferDTO.getUserId());

        TransferRequestDTO transferRequestDTO = accountMapper.transferDTOToTransferRequestDTO(transferDTO);
        AccountResponse accountResponse = accountClient.transfer(transferDTO.getAccountId(), transferRequestDTO);
        TransactionDetailsDTO transactionDetailsDTO = accountMapper.accountResponseToTransactionDetailsDTO(accountResponse);

        transactionDetailsDTO.setStatus(setStatus(transactionDetailsDTO));
        transactionDetailsDTO.setDescription(setUpDescription(transactionDetailsDTO.getDescription()));
        transactionDetailsDTO.setCurrency(setCurrency(transactionDetailsDTO.getCurrency(), transferDTO.getCurrency()));


        return transactionDetailsDTO;
    }
}
