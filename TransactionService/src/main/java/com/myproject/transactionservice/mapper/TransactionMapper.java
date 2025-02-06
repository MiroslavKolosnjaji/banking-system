package com.myproject.transactionservice.mapper;

import com.myproject.transactionservice.client.dto.TransferRequestDTO;
import com.myproject.transactionservice.controller.request.DepositRequest;
import com.myproject.transactionservice.controller.request.TransactionRequest;
import com.myproject.transactionservice.controller.request.TransferRequest;
import com.myproject.transactionservice.controller.request.WithdrawRequest;
import com.myproject.transactionservice.controller.response.TransactionResponse;
import com.myproject.transactionservice.dto.DepositDTO;
import com.myproject.transactionservice.dto.TransactionDTO;
import com.myproject.transactionservice.dto.TransferDTO;
import com.myproject.transactionservice.dto.WithdrawDTO;
import com.myproject.transactionservice.model.Transaction;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * @author Miroslav Kološnjaji
 */
@Mapper
public interface TransactionMapper {
    TransactionDTO transactionToTransactionDTO(Transaction transaction);
    Transaction transactionDTOToTransaction(TransactionDTO transactionDTO);

    TransferRequest transferDTOToTransferRequest(TransferDTO transferDTO);

    TransactionDTO transactionRequestToTransactionDTO(TransactionRequest transactionRequest);
    DepositDTO depositRequestToDepositDTO(DepositRequest depositRequest);
    WithdrawDTO withdrawRequestToWithdrawDTO(WithdrawRequest withdrawRequest);
    TransferDTO transferRequestToTransferDTO(TransferRequest transferRequestDTO);

    @IterableMapping(elementTargetType = TransactionResponse.class)
    List<TransactionResponse> transactionDTOListToTransactionResponseList(List<TransactionDTO> transactionDTOList);

    TransferRequestDTO transferDTOToTransferRequestDTO(TransferDTO transferDTO);

    TransferRequestDTO transferRequestToTransferRequestDTO(TransferRequest transferRequest);

    TransactionResponse transactionDTOToTransactionResponse(TransactionDTO transactionDTO);
}
