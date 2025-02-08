package com.myproject.transactionservice.controller;

import com.myproject.transactionservice.controller.request.DepositRequest;
import com.myproject.transactionservice.controller.request.TransactionRequest;
import com.myproject.transactionservice.controller.request.TransferRequest;
import com.myproject.transactionservice.controller.request.WithdrawRequest;
import com.myproject.transactionservice.controller.response.TransactionResponse;
import com.myproject.transactionservice.dto.DepositDTO;
import com.myproject.transactionservice.dto.TransactionDTO;
import com.myproject.transactionservice.dto.TransferDTO;
import com.myproject.transactionservice.dto.WithdrawDTO;
import com.myproject.transactionservice.exception.controller.EntityNotFoundException;
import com.myproject.transactionservice.mapper.TransactionMapper;
import com.myproject.transactionservice.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Miroslav Kološnjaji
 */
@RestController
@RequestMapping("api/v1/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private static final String PATH_VARIABLE = "transactionId";
    public static final String TRANSACTION_URI = "/api/v1/transaction";
    public static final String TRANSACTION_ID = "/{transactionId}";
    public static final String TRANSACTION_URI_WITH_ID = TRANSACTION_URI + TRANSACTION_ID;

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAllTransactions(){

        List<TransactionDTO> transactionDTOList = transactionService.getAll();
        List<TransactionResponse> transactionResponses = transactionMapper.transactionDTOListToTransactionResponseList(transactionDTOList);

        return ResponseEntity.ok(transactionResponses);
    }

    @GetMapping(TRANSACTION_ID)
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable(PATH_VARIABLE) Long transactionId) throws EntityNotFoundException{

        TransactionDTO transactionDTO = transactionService.getById(transactionId);
        TransactionResponse transactionResponse = transactionMapper.transactionDTOToTransactionResponse(transactionDTO);

        return ResponseEntity.ok(transactionResponse);
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody DepositRequest depositRequest){

        DepositDTO depositDTO = transactionMapper.depositRequestToDepositDTO(depositRequest);
        TransactionDTO transactionDTO = transactionService.deposit(depositDTO);

        TransactionResponse transactionResponse = transactionMapper.transactionDTOToTransactionResponse(transactionDTO);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.LOCATION, TRANSACTION_URI + "/" + transactionDTO.getId());

        return new ResponseEntity<>(transactionResponse, httpHeaders, HttpStatus.CREATED);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody WithdrawRequest withdrawRequest){

        WithdrawDTO withdrawDTO = transactionMapper.withdrawRequestToWithdrawDTO(withdrawRequest);
        TransactionDTO transactionDTO = transactionService.withdraw(withdrawDTO);

        TransactionResponse transactionResponse = transactionMapper.transactionDTOToTransactionResponse(transactionDTO);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.LOCATION, TRANSACTION_URI + "/" + transactionDTO.getId());

        return new ResponseEntity<>(transactionResponse, httpHeaders, HttpStatus.CREATED);
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(@Valid @RequestBody TransferRequest transferRequest) {

        TransferDTO transferDTO = transactionMapper.transferRequestToTransferDTO(transferRequest);
        TransactionDTO transactionDTO = transactionService.transfer(transferDTO);

        TransactionResponse transactionResponse = transactionMapper.transactionDTOToTransactionResponse(transactionDTO);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.LOCATION, TRANSACTION_URI + "/" + transactionDTO.getId());

        return new ResponseEntity<>(transactionResponse, httpHeaders, HttpStatus.CREATED);
    }
}
