package com.myproject.transactionservice.mapper;

import com.myproject.transactionservice.client.dto.DepositRequestDTO;
import com.myproject.transactionservice.client.dto.TransferRequestDTO;
import com.myproject.transactionservice.client.dto.WithdrawRequestDTO;
import com.myproject.transactionservice.client.response.AccountResponse;
import com.myproject.transactionservice.dto.*;
import org.mapstruct.Mapper;

/**
 * @author Miroslav Kološnjaji
 */
@Mapper
public interface AccountMapper {

    TransactionDetailsDTO accountResponseToTransactionDetailsDTO(AccountResponse accountResponse);
    AccountResponse transactionDetailsDTOToAccountResponse(TransactionDetailsDTO transactionDetailsDTO);

    DepositRequestDTO transactionDTOToDepositRequestDTO(TransactionDTO transactionDTO);
    WithdrawRequestDTO transactionDTOToWithdrawRequestDTO(TransactionDTO transactionDTO);
    TransferRequestDTO transferDTOToTransferRequestDTO(TransferDTO transferDTO);

    DepositDTO depositRequestDTOToDepositDTO(DepositRequestDTO depositRequestDTO);
    DepositRequestDTO depositDTOToDepositRequestDTO(DepositDTO depositDTO);

    WithdrawDTO withdrawRequestDTOToWithdrawDTO(WithdrawRequestDTO withdrawRequestDTO);
    WithdrawRequestDTO withdrawDTOToWithdrawRequestDTO(WithdrawDTO withdrawDTO);
}
