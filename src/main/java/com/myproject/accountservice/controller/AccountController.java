package com.myproject.accountservice.controller;

import com.myproject.accountservice.controller.request.*;
import com.myproject.accountservice.controller.response.TransferResponse;
import com.myproject.accountservice.dto.*;
import com.myproject.accountservice.controller.response.AccountResponse;
import com.myproject.accountservice.controller.response.DepositResponse;
import com.myproject.accountservice.controller.response.WithdrawResponse;
import com.myproject.accountservice.exception.controller.AccountStatusViolationException;
import com.myproject.accountservice.exception.controller.EntityNotFoundException;
import com.myproject.accountservice.exception.service.UnauthorizedAccountAccessException;
import com.myproject.accountservice.mapper.AccountMapper;
import com.myproject.accountservice.mapper.TransactionMapper;
import com.myproject.accountservice.service.AccountService;
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
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private static final String ACCOUNT_ID_VAR_NAME = "accountId";
    public static final String ACCOUNT_URI = "/api/v1/account";
    public static final String ACCOUNT_ID = "/{accountId}";
    public static final String ACCOUNT_URI_WITH_ID = ACCOUNT_URI + ACCOUNT_ID;


    private final AccountService accountService;
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;


    @PostMapping
    public ResponseEntity<AccountResponse> save(@Valid @RequestBody AccountRequest request) {

        AccountDTO savedAccount = accountService.save(accountMapper.accountRequestToAccountDTO(request));

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.LOCATION, ACCOUNT_URI + "/" + savedAccount.getId());

        return new ResponseEntity<>(mapToAccountResponse(savedAccount), httpHeaders, HttpStatus.CREATED);
    }

    @PutMapping(ACCOUNT_ID)
    public ResponseEntity<AccountResponse> update(@PathVariable(ACCOUNT_ID_VAR_NAME) Long accountId, @Valid @RequestBody UpdateAccountRequest request) throws EntityNotFoundException {

        AccountDTO updatedAccount = accountService.update(accountId, accountMapper.updateAccountRequestToAccountDTO(request));

        return new ResponseEntity<>(mapToAccountResponse(updatedAccount), HttpStatus.OK);
    }

    @PutMapping("/deposit" + ACCOUNT_ID)
    public ResponseEntity<DepositResponse> deposit(@PathVariable(ACCOUNT_ID_VAR_NAME) Long accountId, @Valid @RequestBody DepositRequest request) throws EntityNotFoundException, AccountStatusViolationException, UnauthorizedAccountAccessException {

        DepositDTO depositDTO = transactionMapper.depositRequestToDepositDTO(request);

        TransactionDTO  transactionDTO = accountService.deposit(accountId, depositDTO);

        return new ResponseEntity<>(transactionMapper.transactionDTOToDepositResponse(transactionDTO), HttpStatus.OK);
    }

    @PutMapping("/withdraw" + ACCOUNT_ID)
    public ResponseEntity<WithdrawResponse> withdraw(@PathVariable(ACCOUNT_ID_VAR_NAME) Long accountId, @Valid @RequestBody WithdrawRequest request) throws EntityNotFoundException, AccountStatusViolationException, UnauthorizedAccountAccessException {

        WithdrawDTO withdrawDTO = transactionMapper.withdrawRequestToWithdrawDTO(request);

        TransactionDTO transactionDTO = accountService.withdraw(accountId, withdrawDTO);

        return new ResponseEntity<>(transactionMapper.transactionDTOToWithdrawResponse(transactionDTO), HttpStatus.OK);
    }

    @PutMapping("/transfer" + ACCOUNT_ID)
    public ResponseEntity<TransferResponse> transfer(@PathVariable(ACCOUNT_ID_VAR_NAME) Long accountId, @Valid @RequestBody TransferRequest request) throws EntityNotFoundException,AccountStatusViolationException, UnauthorizedAccountAccessException {

        TransferDTO transferDTO = transactionMapper.transferRequestToTransferDTO(request);

        TransactionDTO transactionDTO = accountService.transfer(accountId, transferDTO);
        return new ResponseEntity<>(transactionMapper.transactionDTOtoTransferResponse(transactionDTO), HttpStatus.OK);

    }

    @PutMapping("/status/{accountId}")
    public ResponseEntity<AccountResponse> updateStatus(@PathVariable(ACCOUNT_ID_VAR_NAME) Long accountId, @Valid @RequestBody AccountStatusRequest request) throws EntityNotFoundException {

        AccountStatusDTO accountStatusDTO = accountMapper.accountStatusRequestToAccountStatusDTO(request);

        AccountDetailsDTO accountDetailsDTO = accountService.setAccountStatus(accountId, accountStatusDTO);

        return new ResponseEntity<>(mapToAccountResponse(accountDetailsDTO), HttpStatus.OK);
    }

    @GetMapping( "/users/{userId}/accounts")
    public ResponseEntity<List<AccountDTO>> getUserAccounts(@PathVariable("userId") Long userId) {

        List<AccountDTO> accountDTOList = accountService.getAllUserAccounts(userId);
        return new ResponseEntity<>(accountDTOList, HttpStatus.OK);
    }

    @DeleteMapping(ACCOUNT_ID)
    public ResponseEntity<Void> delete(@PathVariable(ACCOUNT_ID_VAR_NAME) Long accountId) throws EntityNotFoundException {
        accountService.delete(accountId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    private AccountResponse mapToAccountResponse(AccountDTO accountDTO){
        return accountMapper.accountDTOToAccountResponse(accountDTO);
    }

    private AccountResponse mapToAccountResponse(AccountDetailsDTO accountDetailsDTO){
        return accountMapper.accountDetailsDTOToAccountResponse(accountDetailsDTO);
    }

}
