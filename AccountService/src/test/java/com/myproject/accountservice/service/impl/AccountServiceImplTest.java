package com.myproject.accountservice.service.impl;

import com.myproject.accountservice.dto.*;
import com.myproject.accountservice.exception.controller.AccountStatusViolationException;
import com.myproject.accountservice.exception.controller.EntityNotFoundException;
import com.myproject.accountservice.exception.service.AccountNotFoundException;
import com.myproject.accountservice.exception.service.UnauthorizedAccountAccessException;
import com.myproject.accountservice.exception.service.status.InsufficientFundsException;
import com.myproject.accountservice.mapper.AccountMapper;
import com.myproject.accountservice.mapper.TransactionMapper;
import com.myproject.accountservice.model.Account;
import com.myproject.accountservice.model.AccountType;
import com.myproject.accountservice.model.Currency;
import com.myproject.accountservice.model.Status;
import com.myproject.accountservice.repository.AccountRepository;
import com.myproject.accountservice.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Miroslav Kološnjaji
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private TransactionMapper transactionMapper;

    @Spy
    @InjectMocks
    private AccountServiceImpl accountService;

    private Account account;
    private AccountDTO accountDTO;
    private DepositDTO depositDTO;
    private WithdrawDTO withdrawDTO;
    private AccountStatusDTO accountStatusDTO;

    @BeforeEach
    void setUp() {

        account = Account.builder()
                .accountType(AccountType.CHECKING)
                .accountNumber("1234")
                .balance(BigDecimal.ZERO)
                .userId(1L)
                .status(Status.ACTIVE)
                .build();

        accountDTO = AccountDTO.builder()
                .accountType(account.getAccountType())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .userId(account.getUserId())
                .status(account.getStatus())
                .build();

        depositDTO = new DepositDTO();
        depositDTO.setUserId(1L);

        withdrawDTO = new WithdrawDTO();
        withdrawDTO.setUserId(1L);

        accountStatusDTO = new AccountStatusDTO();
        accountStatusDTO.setStatus(Status.ACTIVE);

    }

    @DisplayName("Save Account")
    @Test
    void testSaveAccount_whenValidDetailsProvided_returnsAccountResponse() {

        //given
        when(accountMapper.accountDTOToAccount(accountDTO)).thenReturn(account);
        when(accountRepository.findAccountByAccountNumber(anyString())).thenReturn(Optional.empty());
        when(accountMapper.accountToAccountDTO(account)).thenReturn(accountDTO);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        //when
        AccountDTO savedAccount = accountService.save(accountDTO);

        //then
        assertNotNull(savedAccount, "Saved account should not be null");

        verify(accountMapper).accountToAccountDTO(account);
        verify(accountRepository).findAccountByAccountNumber(anyString());
        verify(accountRepository).save(any(Account.class));
    }

    @DisplayName("Update Account")
    @Test
    void testUpdateAccount_whenValidDetailsProvided_returnsAccountResponse() throws AccountNotFoundException {

        //given
        Account account = Account.builder()
                .id(1L)
                .accountNumber("123456")
                .accountType(AccountType.CHECKING)
                .balance(new BigDecimal("123"))
                .build();

        AccountDTO accountDTO = AccountDTO.builder()
                .accountType(account.getAccountType())
                .status(account.getStatus())
                .balance(account.getBalance())
                .build();

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        when(accountMapper.accountToAccountDTO(account)).thenReturn(accountDTO);

        //when
        AccountDTO updatedAccount = accountService.update(1L, accountDTO);

        //then
        assertNotNull(updatedAccount, "Updated Account should not be null");
        assertEquals(new BigDecimal("123"), updatedAccount.getBalance());

        verify(accountRepository).findById(anyLong());
        verify(accountMapper).accountToAccountDTO(account);
    }

    @DisplayName("Update Account Failed - Invalid ID Provided")
    @Test
    void testUpdateAccount_whenInvalidIDProvided_throwsAccountNotFoundException() {

        //given
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        Executable executable = () -> accountService.update(1L, accountDTO);

        //then
        assertThrows(AccountNotFoundException.class, executable, "Exception doesn't match. Expected AccountNotFoundException");

        verify(accountRepository).findById(anyLong());

    }

    @DisplayName("Account Deposit")
    @Test
    void testDeposit_whenValidDetailsProvided_thenReturnAccountResponse() throws EntityNotFoundException, AccountStatusViolationException, UnauthorizedAccountAccessException {

        //given
        depositDTO.setAmount(new BigDecimal("100"));

        account = Account.builder()
                .id(1L)
                .userId(1L)
                .accountNumber("RS30659143710820343355")
                .accountType(AccountType.CHECKING)
                .balance(new BigDecimal("223"))
                .userId(1L)
                .status(Status.ACTIVE)
                .build();

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));

        //when
        TransactionDTO transactionDTO = accountService.deposit(1L, depositDTO);

        //then
        assertNotNull(transactionDTO, "Account details DTO should not be null.");
        assertEquals(new BigDecimal("323"), transactionDTO.getBalance());

        verify(accountRepository).findById(anyLong());
    }

    @DisplayName("Account Deposit Failed - Invalid ID Provided")
    @Test
    void testDeposit_whenInvalidIdProvided_throwsAccountNotFoundException() {

        //given
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        Executable executable = () -> accountService.deposit(1L, depositDTO);

        //then
        assertThrows(AccountNotFoundException.class, executable, "Exception doesn't match. Expected AccountNotFoundException");

        verify(accountRepository).findById(anyLong());
    }

    @DisplayName("Account Deposit Failed - Request User ID doesn't match account Owner ")
    @Test
    void testDeposit_whenUserIdDoesNotMatch_throwsUnauthorizedAccountAccessException() {

        //given
        Account account = Account.builder().userId(2L).build();
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));

        //when
        Executable executable = () -> accountService.deposit(2L, depositDTO);

        //then
        assertThrows(UnauthorizedAccountAccessException.class, executable, "Exception doesn't match. Expected UnauthorizedAccountAccessException.");
    }

    @DisplayName("Account Deposit Failed - Account Status ")
    @ParameterizedTest(name = "Deposit attempt with status {0} should throw AccountStatusViolationException")
    @EnumSource(value = Status.class, names = {"FROZEN", "BLOCKED", "RESTRICTED", "CLOSED"})
    void testDeposit_whenAccountStatusViolated_throwsAccountStatusViolationException(Status status) {

        //given
        Account account = Account.builder().userId(1L).status(status).build();

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));

        //when
        Executable executable = () -> accountService.deposit(1L, depositDTO);

        //then
        assertThrows(AccountStatusViolationException.class, executable, "Exception doesn't match. Excepted exception that inherit AccountStatusViolationException");

        verify(accountRepository).findById(anyLong());
    }

    @DisplayName("Account Withdraw")
    @Test
    void testWithdraw_whenValidDetailsProvided_thenReturnAccountResponse() throws EntityNotFoundException, AccountStatusViolationException, InsufficientFundsException, UnauthorizedAccountAccessException {

        //given
        withdrawDTO.setAmount(new BigDecimal("100"));

        Account account = Account.builder()
                .id(1L)
                .accountNumber("RS30659143710820343355")
                .accountType(AccountType.CHECKING)
                .balance(new BigDecimal("123"))
                .userId(1L)
                .status(Status.ACTIVE)
                .build();

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));

        //when
        TransactionDTO transactionDTO = accountService.withdraw(1L, withdrawDTO);

        //then
        assertNotNull(transactionDTO, "Account details DTO should not be null.");
        assertEquals(new BigDecimal("23"), transactionDTO.getBalance(), "Balance not match.");

        verify(accountRepository).findById(anyLong());
    }

    @DisplayName("Account Withdraw Failed - Invalid ID Provided")
    @Test
    void testWithdraw_whenInvalidIdProvided_throwsAccountNotFoundException() {

        //given
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        Executable executable = () -> accountService.withdraw(1L, withdrawDTO);

        //then
        assertThrows(AccountNotFoundException.class, executable, "Exception doesn't match. Expected AccountNotFoundException");

        verify(accountRepository).findById(anyLong());
    }

    @DisplayName("Account Withdraw Failed - Insufficient Funds")
    @Test
    void testWithdraw_whenInsufficientFundsReached_throwsInsufficientFundsException() {

        //given
        withdrawDTO.setAmount(new BigDecimal("50"));

        Account account = Account.builder().balance(new BigDecimal("0")).status(Status.ACTIVE).userId(1L).build();
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));

        //when
        Executable executable = () -> accountService.withdraw(1L, withdrawDTO);

        assertThrows(InsufficientFundsException.class, executable, "Exception doesn't match. Excepted InsufficientFundsException");

        verify(accountRepository).findById(anyLong());

    }

    @DisplayName("Account Withdraw Failed - Request User ID doesn't match account Owner")
    @Test
    void testWithdraw_whenUserIdDoesNotMatch_throwsUnauthorizedAccountAccessException() {

        //given
        Account account = Account.builder().userId(2L).build();
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));

        //when
        Executable executable = () -> accountService.withdraw(1L, withdrawDTO);

        //then
        assertThrows(UnauthorizedAccountAccessException.class, executable, "Exception doesn't match. Expected UnauthorizedAccountAccessException.");

        verify(accountRepository).findById(anyLong());
    }

    @DisplayName("Account Withdraw Failed - Account Status ")
    @ParameterizedTest(name = "Withdraw attempt with status {0} should throw AccountStatusViolationException")
    @EnumSource(value = Status.class, names = {"FROZEN", "BLOCKED", "RESTRICTED", "CLOSED"})
    void testWithdraw_whenAccountStatusViolated_throwsAccountStatusViolationException(Status status) {

        //given
        Account account = Account.builder().userId(1L).status(status).build();
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));

        //when
        Executable executable = () -> accountService.withdraw(1L, withdrawDTO);

        //then
        assertThrows(AccountStatusViolationException.class, executable, "Exception doesn't match. Excepted exception that inherit AccountStatusViolationException");

        verify(accountRepository).findById(anyLong());
    }

    @DisplayName("Account Fund Transfer ")
    @Test
    void testTransfer_whenValidDetailsProvided_thenReturnTransactionDTO() throws Exception {

        String senderAccountNumber = "967495685746948763453234223423";
        String recipientAccountNumber = "398425987285723052032572305234";

        Account account = Account.builder()
                .id(1L)
                .userId(2L)
                .accountNumber(senderAccountNumber)
                .status(Status.ACTIVE)
                .balance(new BigDecimal("10000"))
                .currency(Currency.RSD)
                .build();

        TransferDTO transferDTO = TransferDTO.builder()
                .userId(1L)
                .recipientAccountNumber(recipientAccountNumber)
                .currency(Currency.RSD)
                .amount(new BigDecimal("5000"))
                .build();


        TransactionDTO transactionDTO = TransactionDTO.builder()
                .userId(2L)
                .amount(transferDTO.getAmount())
                .balance(new BigDecimal("10000"))
                .build();

        WithdrawDTO withdrawDTO = new WithdrawDTO(1L, transferDTO.getAmount(), transferDTO.getCurrency());
        DepositDTO depositDTO = new DepositDTO(2L, transactionDTO.getAmount(), account.getCurrency());

        when(accountRepository.findAccountByAccountNumber(transferDTO.getRecipientAccountNumber())).thenReturn(Optional.of(account));
        doReturn(transactionDTO).when(accountService).withdraw(1L, withdrawDTO);
        doReturn(transactionDTO).when(accountService).deposit(1L, depositDTO);


        TransactionDTO transactionDTO1 = accountService.transfer(1L, transferDTO);

        assertNotNull(transactionDTO1, "TransactionDTO should not be null.");

        verify(accountRepository).findAccountByAccountNumber(transferDTO.getRecipientAccountNumber());
        verify(accountService).withdraw(1L, withdrawDTO);
        verify(accountService).deposit(1L, depositDTO);

    }

    @DisplayName("Account Fund Transfer Failed - Not enough funds on account ")
    @Test
    void testTransfer_whenNotEnoughFundsOnAccount_thenThrowInsufficientFundsException() throws Exception {

        String senderAccountNumber = "967495685746948763453234223423";
        String recipientAccountNumber = "398425987285723052032572305234";

        Account account = Account.builder()
                .id(1L)
                .userId(2L)
                .accountNumber(senderAccountNumber)
                .status(Status.ACTIVE)
                .balance(new BigDecimal("10000"))
                .currency(Currency.RSD)
                .build();

        TransferDTO transferDTO = TransferDTO.builder()
                .userId(1L)
                .recipientAccountNumber(recipientAccountNumber)
                .currency(Currency.RSD)
                .amount(new BigDecimal("5000"))
                .build();

        WithdrawDTO withdrawDTO = new WithdrawDTO(1L, transferDTO.getAmount(), transferDTO.getCurrency());

        when(accountRepository.findAccountByAccountNumber(transferDTO.getRecipientAccountNumber())).thenReturn(Optional.of(account));
        doThrow(InsufficientFundsException.class).when(accountService).withdraw(1L, withdrawDTO);


        Executable executable = () -> accountService.transfer(1L, transferDTO);


        assertThrows(InsufficientFundsException.class, executable, "Expected InsufficientFundsException.");

        verify(accountRepository).findAccountByAccountNumber(transferDTO.getRecipientAccountNumber());
    }

    @DisplayName("Set Account Status")
    @Test
    void testSetAccountStatus_whenValidDetailsProvided_thenReturnAccountResponse() throws AccountNotFoundException {

        //given
        Account account = Account.builder().build();
        AccountDetailsDTO accountDetailsDTO = new AccountDetailsDTO();

        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        when(accountMapper.accountToAccountDetails(account)).thenReturn(accountDetailsDTO);

        //when
        AccountDetailsDTO accountDetailsDTO1 = accountService.setAccountStatus(1L, accountStatusDTO);

        //then
        assertNotNull(accountDetailsDTO1, "Account Response should not be null.");

        verify(accountRepository).findById(anyLong());
        verify(accountMapper).accountToAccountDetails(account);
    }

    @DisplayName("Set Account Status Failed - Invalid ID Provided")
    @Test
    void testSetAccountStatus_whenInvalidIdProvided_throwsAccountNotFoundException() {

        //given
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        Executable executable = () -> accountService.setAccountStatus(1L, accountStatusDTO);

        //then
        assertThrows(AccountNotFoundException.class, executable, "Exception doesn't match. Expected AccountNotFoundException.");

        verify(accountRepository).findById(anyLong());
    }

    @DisplayName("Get All User Accounts")
    @Test
    void testGetAllUserAccounts_whenUserIdProvided_thenReturnAccountDTOList() {

        //given
        List<Account> accountList = List.of(mock(Account.class), mock(Account.class), mock(Account.class));

        when(accountRepository.findAccountsByUserId(anyLong())).thenReturn(accountList);

        //when
        List<AccountDTO> list = accountService.getAllUserAccounts(anyLong());

        //then
        assertNotNull(list, "List should not be null.");
        assertEquals(3, list.size(), "List size should match expected value.");

        verify(accountRepository).findAccountsByUserId(anyLong());
    }

    @DisplayName("Delete Account By ID")
    @Test
    void testDeleteAccount_whenValidIdProvided_thenCorrect() throws AccountNotFoundException {

        //given
        when(accountRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(accountRepository).deleteById(anyLong());

        //when
        accountService.delete(anyLong());

        //then
        verify(accountRepository).existsById(anyLong());
        verify(accountRepository).deleteById(anyLong());
    }

    @DisplayName("Delete Account By ID Failed - Invalid ID Provided")
    @Test
    void testDeleteAccount_whenInvalidIdProvided_throwsAccountNotFoundException() {

        //given
        when(accountRepository.existsById(anyLong())).thenReturn(false);

        //when
        Executable executable = () -> accountService.delete(anyLong());

        //then
        assertThrows(AccountNotFoundException.class, executable, "Exception doesn't match. Expected AccountNotFoundException");

        verify(accountRepository).existsById(anyLong());
    }
}