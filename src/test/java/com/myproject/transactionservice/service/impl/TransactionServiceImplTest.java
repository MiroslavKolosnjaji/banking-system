package com.myproject.transactionservice.service.impl;

import com.myproject.transactionservice.dto.*;
import com.myproject.transactionservice.exception.service.TransactionNotFoundException;
import com.myproject.transactionservice.exception.service.strategy.UserNotFoundException;
import com.myproject.transactionservice.mapper.TransactionMapper;
import com.myproject.transactionservice.service.NotificationService;
import com.myproject.transactionservice.model.Status;
import com.myproject.transactionservice.model.Transaction;
import com.myproject.transactionservice.model.TransactionType;
import com.myproject.transactionservice.repository.TransactionRepository;
import com.myproject.transactionservice.service.strategy.TransactionStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/**
 * @author Miroslav Kološnjaji
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private NotificationService notificationService;

    @Mock
    TransactionStrategyFactory transactionStrategyFactory;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private TransactionDTO transactionDTO;
    private final String DESCRIPTION = "Transaction successfully performed.";

    @BeforeEach
    void setUp() {
        transactionDTO = TransactionDTO.builder()
                .id(1L)
                .userId(1L)
                .accountId(1L)
                .transactionType(TransactionType.DEPOSIT)
                .status(Status.SUCCESS)
                .description(DESCRIPTION)
                .build();
    }

    @DisplayName("Transaction - Deposit")
    @Test
    void testDeposit_whenValidDetailsProvided_returnsTransactionDTO() throws Exception {

        DepositDTO depositDTO = DepositDTO.builder().userId(1L).accountId(1L).build();
        TransactionDetailsDTO transactionDetailsDTO = TransactionDetailsDTO.builder().build();

        when(transactionStrategyFactory.performTransaction(depositDTO)).thenReturn(transactionDetailsDTO);
        when(transactionService.save(transactionDTO)).thenReturn(transactionDTO);
        doNothing().when(notificationService).sendNotification(transactionDetailsDTO, transactionDTO);

        TransactionDTO depositTransaction = transactionService.deposit(depositDTO);

        assertNotNull(depositTransaction, "Deposit transaction should not be null");

        verify(transactionStrategyFactory).performTransaction(depositDTO);
        verify(notificationService).sendNotification(transactionDetailsDTO, transactionDTO);

    }

    @DisplayName("Transaction - Deposit FAILED - User Not Found")
    @Test
    void testDeposit_whenUserDoesntExists_thenThrowsUserNotFoundException() throws Exception {

        DepositDTO depositDTO = DepositDTO.builder().userId(1L).accountId(1L).build();

        when(transactionStrategyFactory.performTransaction(depositDTO)).thenThrow(RuntimeException.class);

        Executable executable = () -> transactionService.deposit(depositDTO);

        assertThrows(RuntimeException.class, executable, "Exception mismatch. Expected RuntimeException.");

        verify(transactionStrategyFactory).performTransaction(depositDTO);


    }

    @DisplayName("Transaction - Withdraw")
    @Test
    void testWithdraw_whenValidDetailsProvided_returnsTransactionDTO() throws Exception {

        WithdrawDTO withdrawDTO = WithdrawDTO.builder().userId(1L).accountId(1L).build();
        TransactionDetailsDTO transactionDetailsDTO = TransactionDetailsDTO.builder().build();

        when(transactionStrategyFactory.performTransaction(withdrawDTO)).thenReturn(transactionDetailsDTO);
        when(transactionService.save(transactionDTO)).thenReturn(transactionDTO);
        doNothing().when(notificationService).sendNotification(transactionDetailsDTO, transactionDTO);

        TransactionDTO withdrawTransaction = transactionService.withdraw(withdrawDTO);

        assertNotNull(withdrawTransaction, "Withdraw transaction should not be null");

        verify(transactionStrategyFactory).performTransaction(withdrawDTO);
        verify(notificationService).sendNotification(transactionDetailsDTO, transactionDTO);
    }

    @DisplayName("Transaction - Transfer FAILED - User Not Found")
    @Test
    void testWithdraw_whenUserDoesntExists_thenThrowsUserNotFoundException() throws Exception {

        WithdrawDTO withdrawDTO = WithdrawDTO.builder().userId(1L).accountId(1L).build();

        when(transactionStrategyFactory.performTransaction(withdrawDTO)).thenThrow(RuntimeException.class);

        Executable executable = () -> transactionService.withdraw(withdrawDTO);

        assertThrows(RuntimeException.class, executable, "Exception mismatch. Expected RuntimeException.");

        verify(transactionStrategyFactory).performTransaction(withdrawDTO);


    }

    @DisplayName("Transaction - Transfer")
    @Test
    void testTransfer_whenValidDetailsProvided_returnsTransactionDTO() throws Exception {

        TransferDTO transferDTO = TransferDTO.builder().userId(1L).accountId(1L).build();
        TransactionDetailsDTO transactionDetailsDTO = TransactionDetailsDTO.builder().build();

        when(transactionStrategyFactory.performTransaction(transferDTO)).thenReturn(transactionDetailsDTO);
        when(transactionService.save(transactionDTO)).thenReturn(transactionDTO);
        doNothing().when(notificationService).sendNotification(transactionDetailsDTO, transactionDTO);

        TransactionDTO transferTransaction = transactionService.transfer(transferDTO);

        assertNotNull(transferTransaction, "Transfer transaction should not be null");


        verify(transactionStrategyFactory).performTransaction(transferDTO);
        verify(notificationService).sendNotification(transactionDetailsDTO, transactionDTO);

    }

    @DisplayName("Transaction - Transfer FAILED - User Not Found")
    @Test
    void testTransfer_whenUserDoesntExists_thenThrowsUserNotFoundException() throws Exception {

        TransferDTO transferDTO = TransferDTO.builder().userId(1L).accountId(1L).build();

        when(transactionStrategyFactory.performTransaction(transferDTO)).thenThrow(RuntimeException.class);

        Executable executable = () -> transactionService.transfer(transferDTO);

        assertThrows(RuntimeException.class, executable, "Exception mismatch. Expected RuntimeException.");

        verify(transactionStrategyFactory).performTransaction(transferDTO);


    }

    @DisplayName("Get All Transactions")
    @Test
    void testGetAll_whenListIsPopulated_returnsListOfTransactionDTO() {

        //given
        List<Transaction> transactions = List.of(getMockTransaction(), getMockTransaction(), getMockTransaction());

        when(transactionRepository.findAll()).thenReturn(transactions);
        when(transactionMapper.transactionToTransactionDTO(any(Transaction.class))).thenReturn(transactionDTO);

        //when
        List<TransactionDTO> list = transactionService.getAll();

        //then
        assertNotNull(list, "List should not be null.");
        assertEquals(3, list.size(), "List size should be 3.");

        verify(transactionRepository).findAll();
        verify(transactionMapper, times(3)).transactionToTransactionDTO(any(Transaction.class));

    }

    @DisplayName("Get Transaction by ID")
    @Test
    void testGetById_whenValidIdProvided_returnsTransactionDTO() throws TransactionNotFoundException {

        //given
        Transaction transaction = getMockTransaction();

        when(transactionRepository.findById(anyLong())).thenReturn(Optional.of(transaction));
        when(transactionMapper.transactionToTransactionDTO(transaction)).thenReturn(transactionDTO);

        //when
        TransactionDTO foundTransaction = transactionService.getById(1L);

        //then
        assertNotNull(foundTransaction, "Found transaction should not be null.");
        assertEquals(foundTransaction, transactionDTO, "Found transaction doesn't match expected transaction.");

        verify(transactionRepository).findById(anyLong());
        verify(transactionMapper).transactionToTransactionDTO(transaction);
    }

    @DisplayName("Get Transaction By ID Failed - ID not found")
    @Test
    void testGetById_whenIdNotFound_throwsTransactionNotFoundException() {

        //given
        when(transactionRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        Executable executable = () -> transactionService.getById(1L);

        //then
        assertThrows(TransactionNotFoundException.class, executable, "Exception doesn't match. Expected TransactionNotFoundException.");
    }

    private Transaction getMockTransaction() {
        return mock(Transaction.class);
    }
}