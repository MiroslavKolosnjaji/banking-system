package com.myproject.transactionservice.service.strategy;

import com.myproject.transactionservice.client.account.AccountClient;
import com.myproject.transactionservice.client.dto.DepositRequestDTO;
import com.myproject.transactionservice.client.response.AccountResponse;
import com.myproject.transactionservice.client.user.UserClient;
import com.myproject.transactionservice.dto.DepositDTO;
import com.myproject.transactionservice.dto.TransactionDetailsDTO;
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
class DepositStrategyTest {

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private AccountClient accountClient;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private DepositStrategy depositStrategy;

    @DisplayName("DepositStrategy - Perform transaction")
    @Test
    void testPerformTransaction_whenValidDetailsProvided_returnsTransactionDetailsDTO() throws UserNotFoundException {

        DepositDTO depositDTO = DepositDTO.builder().userId(1L).build();
        DepositRequestDTO depositRequestDTO = new DepositRequestDTO();
        AccountResponse accountResponse = AccountResponse.builder().build();
        TransactionDetailsDTO transactionDetailsDTO = TransactionDetailsDTO.builder().build();

        when(userClient.isUserExists(anyLong())).thenReturn(true);
        when(accountMapper.depositDTOToDepositRequestDTO(depositDTO)).thenReturn(depositRequestDTO);
        when(accountClient.deposit(depositDTO.getAccountId(), depositRequestDTO)).thenReturn(accountResponse);
        when(accountMapper.accountResponseToTransactionDetailsDTO(accountResponse)).thenReturn(transactionDetailsDTO);


        TransactionDetailsDTO performedTransaction = depositStrategy.performTransaction(depositDTO);

        assertNotNull(performedTransaction, "Performed (DEPOSIT) transaction should not be null");

        verify(userClient).isUserExists(anyLong());
        verify(accountMapper).depositDTOToDepositRequestDTO(depositDTO);
        verify(accountClient).deposit(depositDTO.getAccountId(), depositRequestDTO);
        verify(accountMapper).accountResponseToTransactionDetailsDTO(accountResponse);

    }

    @DisplayName("DepositStrategy - Perform transaction FAILED")
    @Test
    void testPerformTransaction_whenUserIdNotFound_thenThrowsUserNotFoundException() throws UserNotFoundException {

        DepositDTO depositDTO = DepositDTO.builder().userId(1L).build();

        when(userClient.isUserExists(anyLong())).thenThrow(RuntimeException.class);

        Executable executable = () -> depositStrategy.performTransaction(depositDTO);

        assertThrows(RuntimeException.class, executable, "Exception mismatch. Expected RuntimeException.");

        verify(userClient).isUserExists(anyLong());
    }
}