package com.myproject.accountservice.service;

import com.myproject.accountservice.dto.*;
import com.myproject.accountservice.exception.controller.AccountStatusViolationException;
import com.myproject.accountservice.exception.service.UnauthorizedAccountAccessException;
import com.myproject.accountservice.exception.service.AccountNotFoundException;

import java.util.List;

/**
 * @author Miroslav Kološnjaji
 */
public interface AccountService {

    AccountDTO save(AccountDTO accountDTO);

    AccountDTO update(Long id, AccountDTO accountDTO) throws AccountNotFoundException;

    TransactionDTO deposit(Long id, DepositDTO depositDTO) throws  AccountNotFoundException, AccountStatusViolationException, UnauthorizedAccountAccessException;

    TransactionDTO withdraw(Long id, WithdrawDTO withdrawDTO) throws AccountNotFoundException, AccountStatusViolationException, UnauthorizedAccountAccessException;

    TransactionDTO transfer(Long id, TransferDTO transferDTO) throws AccountNotFoundException, AccountStatusViolationException, UnauthorizedAccountAccessException;

    AccountDetailsDTO setAccountStatus(Long id, AccountStatusDTO accountStatusDTO) throws AccountNotFoundException;

    List<AccountDTO> getAllUserAccounts(Long id);

    void delete(Long id) throws AccountNotFoundException;
}
