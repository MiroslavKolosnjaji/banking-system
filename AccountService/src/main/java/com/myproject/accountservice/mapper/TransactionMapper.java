package com.myproject.accountservice.mapper;

import com.myproject.accountservice.controller.request.TransferRequest;
import com.myproject.accountservice.controller.response.TransferResponse;
import com.myproject.accountservice.dto.DepositDTO;
import com.myproject.accountservice.dto.TransactionDTO;
import com.myproject.accountservice.dto.TransferDTO;
import com.myproject.accountservice.dto.WithdrawDTO;
import com.myproject.accountservice.controller.request.DepositRequest;
import com.myproject.accountservice.controller.request.WithdrawRequest;
import com.myproject.accountservice.controller.response.DepositResponse;
import com.myproject.accountservice.controller.response.WithdrawResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Miroslav Kološnjaji
 */
@Mapper(componentModel = "spring")
public interface TransactionMapper {

    DepositRequest depositDTOToDepositRequest(DepositDTO depositDTO);
    DepositDTO depositRequestToDepositDTO(DepositRequest depositRequest);

    @Mapping(target = "moneyInflow", source = "amount")
    DepositResponse transactionDTOToDepositResponse(TransactionDTO accountDetailsDTO);

    @Mapping(target = "amount", source = "moneyInflow")
    TransactionDTO depositResponseToTransactionDTO(DepositResponse depositResponse);

    WithdrawRequest withdrawDTOToWithdrawRequest(WithdrawDTO withdrawDTO);
    WithdrawDTO withdrawRequestToWithdrawDTO(WithdrawRequest withdrawRequest);

    @Mapping(target = "moneyOutflow", source = "amount")
    WithdrawResponse transactionDTOToWithdrawResponse(TransactionDTO accountDetailsDTO);
    TransactionDTO withdrawResponseToTransactionDTO(WithdrawResponse withdrawResponse);


    @Mapping(target = "moneyOutflow", source = "amount")
    TransferResponse transactionDTOtoTransferResponse(TransactionDTO transactionDTO);
    TransferDTO transferRequestToTransferDTO(TransferRequest transferRequest);

}
