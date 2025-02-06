package com.myproject.transactionservice.service.strategy;

import com.myproject.transactionservice.client.account.AccountClient;
import com.myproject.transactionservice.client.dto.WithdrawRequestDTO;
import com.myproject.transactionservice.client.response.AccountResponse;
import com.myproject.transactionservice.client.user.UserClient;
import com.myproject.transactionservice.dto.TransactionDetailsDTO;
import com.myproject.transactionservice.dto.WithdrawDTO;
import com.myproject.transactionservice.exception.service.strategy.UserNotFoundException;
import com.myproject.transactionservice.mapper.AccountMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Miroslav Kološnjaji
 */
@ExtendWith(MockitoExtension.class)
class WithdrawStrategyTest {

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private AccountClient accountClient;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private WithdrawStrategy withdrawStrategy;

    @Test
    void testPerformTransaction_whenValidDetailsProvided_returnsTransactionDetailsDTO() throws UserNotFoundException {

        WithdrawDTO withdrawDTO = WithdrawDTO.builder().userId(1L).build();
        WithdrawRequestDTO withdrawRequestDTO = new WithdrawRequestDTO();
        AccountResponse accountResponse = AccountResponse.builder().build();
        TransactionDetailsDTO transactionDetailsDTO = TransactionDetailsDTO.builder().build();

        when(userClient.isUserExists(anyLong())).thenReturn(true);
        when(accountMapper.withdrawDTOToWithdrawRequestDTO(withdrawDTO)).thenReturn(withdrawRequestDTO);
        when(accountClient.withdraw(withdrawDTO.getAccountId(), withdrawRequestDTO)).thenReturn(accountResponse);
        when(accountMapper.accountResponseToTransactionDetailsDTO(accountResponse)).thenReturn(transactionDetailsDTO);


        TransactionDetailsDTO performedTransaction = withdrawStrategy.performTransaction(withdrawDTO);

        assertNotNull(performedTransaction, "Performed (WITDRAW) transaction should not be null");

        verify(userClient).isUserExists(anyLong());
        verify(accountMapper).withdrawDTOToWithdrawRequestDTO(withdrawDTO);
        verify(accountClient).withdraw(withdrawDTO.getAccountId(), withdrawRequestDTO);
        verify(accountMapper).accountResponseToTransactionDetailsDTO(accountResponse);


    }

    @DisplayName("WithdrawStrategy - Perform transaction FAILED")
    @Test
    void testPerformTransaction_whenUserIdNotFound_thenThrowsUserNotFoundException() throws UserNotFoundException {

        WithdrawDTO withdrawDTO = WithdrawDTO.builder().userId(1L).build();

        when(userClient.isUserExists(anyLong())).thenThrow(RuntimeException.class);

        Executable executable = () -> withdrawStrategy.performTransaction(withdrawDTO);

        assertThrows(RuntimeException.class, executable, "Exception mismatch. Expected RuntimeException.");

        verify(userClient).isUserExists(anyLong());
    }


}