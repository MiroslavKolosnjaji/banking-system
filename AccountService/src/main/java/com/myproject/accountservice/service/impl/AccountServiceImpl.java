package com.myproject.accountservice.service.impl;

import com.myproject.accountservice.dto.*;
import com.myproject.accountservice.exception.controller.AccountStatusViolationException;
import com.myproject.accountservice.exception.service.CurrencyMismatchException;
import com.myproject.accountservice.exception.service.UnauthorizedAccountAccessException;
import com.myproject.accountservice.exception.service.status.InsufficientFundsException;
import com.myproject.accountservice.exception.service.status.AccountBlockedException;
import com.myproject.accountservice.exception.service.status.AccountClosedException;
import com.myproject.accountservice.exception.service.status.AccountFrozenException;
import com.myproject.accountservice.exception.service.status.AccountRestrictedException;
import com.myproject.accountservice.mapper.TransactionMapper;
import com.myproject.accountservice.model.Currency;
import com.myproject.accountservice.model.Status;
import com.myproject.accountservice.exception.service.AccountNotFoundException;
import com.myproject.accountservice.mapper.AccountMapper;
import com.myproject.accountservice.model.Account;
import com.myproject.accountservice.repository.AccountRepository;
import com.myproject.accountservice.service.AccountService;
import com.myproject.accountservice.util.IBANGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author Miroslav Kološnjaji
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private static final BigDecimal INITIAL_BALANCE = BigDecimal.ZERO;

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public AccountDTO save(AccountDTO accountDTO) {

        Account account = accountMapper.accountDTOToAccount(accountDTO);
        account.setAccountNumber(getIBAN());
        account.setBalance(INITIAL_BALANCE);
        account.setStatus(Status.ACTIVE);

        Account savedAccount = accountRepository.save(account);

        return accountMapper.accountToAccountDTO(savedAccount);
    }

    @Transactional
    @Override
    public AccountDTO update(Long id, AccountDTO accountDTO) throws AccountNotFoundException {

        Account account = getById(id);

        account.setUserId(accountDTO.getUserId());
        account.setAccountNumber(accountDTO.getAccountNumber());
        account.setAccountType(accountDTO.getAccountType());
        account.setStatus(accountDTO.getStatus());

        return accountMapper.accountToAccountDTO(account);
    }

    @Transactional
    @Override
    public TransactionDTO deposit(Long id, DepositDTO depositDTO) throws AccountNotFoundException, AccountStatusViolationException, UnauthorizedAccountAccessException {

        Account account = getVerifiedAccount(id, depositDTO.getUserId());

        BigDecimal currentBalance = account.getBalance();
        account.setBalance(currentBalance.add(depositDTO.getAmount()));

        return TransactionDTO.builder().userId(depositDTO.getUserId()).accountNumber(hideAccountNumber(account.getAccountNumber())).amount(depositDTO.getAmount()).currency(account.getCurrency()).balance(account.getBalance()).status(TransactionStatus.SUCCESS).build();
    }

    @Transactional
    @Override
    public TransactionDTO withdraw(Long id, WithdrawDTO withdrawDTO) throws AccountNotFoundException, AccountStatusViolationException, UnauthorizedAccountAccessException {

        Account account = getVerifiedAccount(id, withdrawDTO.getUserId());

        BigDecimal currentBalance = account.getBalance();
        if (currentBalance.compareTo(withdrawDTO.getAmount()) < 0)
            throw new InsufficientFundsException("Insufficient funds for account " + account.getAccountNumber() + ": Cannot withdraw " + withdrawDTO.getAmount() + ", current balance is " + account.getBalance() + ".");

        account.setBalance(currentBalance.subtract(withdrawDTO.getAmount()));

        return TransactionDTO.builder().userId(withdrawDTO.getUserId()).accountNumber(hideAccountNumber(account.getAccountNumber())).amount(withdrawDTO.getAmount()).currency(account.getCurrency()).balance(account.getBalance()).status(TransactionStatus.SUCCESS).build();
    }

    @Transactional
    @Override
    public TransactionDTO transfer(Long id, TransferDTO transferDTO) throws AccountNotFoundException, AccountStatusViolationException, UnauthorizedAccountAccessException {

        Account recipientAccount = getRecipientAccount(transferDTO.getRecipientAccountNumber());
        validateTransfer(recipientAccount, transferDTO.getCurrency());

        try {

            TransactionDTO senderTransaction = withdraw(id, new WithdrawDTO(transferDTO.getUserId(), transferDTO.getAmount()));
            deposit(recipientAccount.getId(), new DepositDTO(recipientAccount.getUserId(), senderTransaction.getAmount()));

            return senderTransaction;

        } catch (InsufficientFundsException e) {
            throw new InsufficientFundsException("Transaction cannot be performed: insufficient funds");
        }

    }

    @Transactional
    @Override
    public AccountDetailsDTO setAccountStatus(Long id, AccountStatusDTO accountRequest) throws AccountNotFoundException {

        Account account = getById(id);

        account.setStatus(accountRequest.getStatus());

        return accountMapper.accountToAccountDetails(account);
    }

    @Transactional(readOnly = true)
    @Override
    public List<AccountDTO> getAllUserAccounts(Long id) {
        return accountRepository.findAccountsByUserId(id).stream()
                .map(accountMapper::accountToAccountDTO).toList();
    }

    @Override
    public void delete(Long id) throws AccountNotFoundException {

        if (!accountRepository.existsById(id))
            throw new AccountNotFoundException("Account with id " + id + " not found.");

        accountRepository.deleteById(id);
    }

    private void validateTransfer(Account account, Currency currency) throws AccountRestrictedException, AccountBlockedException, AccountFrozenException, AccountClosedException, CurrencyMismatchException {
        checkAccountStatus(account.getStatus());
        checkCurrencyCompatibility(currency,account.getCurrency());
    }

    private void checkCurrencyCompatibility(Currency senderCurrency, Currency recipientCurrency) throws CurrencyMismatchException {
        if (senderCurrency != recipientCurrency)
            throw new CurrencyMismatchException("Transaction cannot be performed: currency mismatch (FROM: " + senderCurrency.name() + " TO: " + recipientCurrency.name() + ")");
    }

    private Account getRecipientAccount(String accountNumber) throws AccountNotFoundException {
        return accountRepository.findAccountByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Recipient account not found."));
    }

    private Account getVerifiedAccount(Long accountId, Long userId) throws AccountNotFoundException, AccountStatusViolationException, UnauthorizedAccountAccessException {

        Account account = getById(accountId);
        verifyUser(account, userId);
        checkAccountStatus(account.getStatus());

        return account;
    }

    private Account getById(Long id) throws AccountNotFoundException {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account with id " + id + " not found."));
    }

    private String hideAccountNumber(String accountNumber) {
        return accountNumber.substring(5, 10) + "****" + accountNumber.substring(16, 18);
    }


    private String getIBAN() {
        String accountNumber = IBANGenerator.generateIBAN();
        Optional<Account> existingAccount = accountRepository.findAccountByAccountNumber(accountNumber);

        while (existingAccount.isPresent()) {
            accountNumber = IBANGenerator.generateIBAN();
            existingAccount = accountRepository.findAccountByAccountNumber(accountNumber);
        }

        return accountNumber;
    }

    private void verifyUser(Account account, Long userId) throws UnauthorizedAccountAccessException {
        if (!account.getUserId().equals(userId)) {
            log.info("User ID: {}, owner ID {}", userId, account.getUserId());
            throw new UnauthorizedAccountAccessException("User ID in request does not match the account owner.");
        }
    }

    private void checkAccountStatus(Status status) throws AccountRestrictedException, AccountBlockedException, AccountFrozenException, AccountClosedException {
        switch (status) {
            case RESTRICTED ->
                    throw new AccountRestrictedException("Transaction not permitted: The account is restricted from performing any transactions.");
            case BLOCKED ->
                    throw new AccountBlockedException("Transaction cannot be processed: The account is blocked and cannot perform any operations.");
            case FROZEN ->
                    throw new AccountFrozenException("Transaction not allowed: The account is frozen, and withdrawals or deposits are prohibited.");
            case CLOSED ->
                    throw new AccountClosedException("Account is no longer active: The account has been closed and cannot be used for transactions.");
        }
    }
}
