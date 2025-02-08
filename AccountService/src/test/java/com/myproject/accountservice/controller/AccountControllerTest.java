package com.myproject.accountservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.accountservice.controller.request.*;
import com.myproject.accountservice.controller.response.TransferResponse;
import com.myproject.accountservice.dto.*;
import com.myproject.accountservice.controller.response.AccountResponse;
import com.myproject.accountservice.controller.response.DepositResponse;
import com.myproject.accountservice.controller.response.WithdrawResponse;
import com.myproject.accountservice.exception.service.AccountNotFoundException;
import com.myproject.accountservice.exception.service.UnauthorizedAccountAccessException;
import com.myproject.accountservice.exception.service.status.*;
import com.myproject.accountservice.mapper.AccountMapper;
import com.myproject.accountservice.mapper.TransactionMapper;
import com.myproject.accountservice.model.AccountType;
import com.myproject.accountservice.model.Currency;
import com.myproject.accountservice.model.Status;
import com.myproject.accountservice.service.AccountService;
import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * @author Miroslav Kološnjaji
 */
@WebMvcTest(controllers = AccountController.class)
@MockBean(AccountService.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    @MockBean
    private AccountMapper accountMapper;

    @MockBean
    private TransactionMapper transactionMapper;

    private ObjectMapper objectMapper;
    private AccountRequest accountRequest;
    private UpdateAccountRequest updateAccountRequest;
    private DepositRequest depositRequest;
    private WithdrawRequest withdrawRequest;

    private String getStatusMessage(int statusCode) {
        return "Incorrect status code returned, status code " + statusCode + " expected.";
    }

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        accountRequest = new AccountRequest();
        accountRequest.setUserId(1L);
        accountRequest.setAccountType(AccountType.CHECKING);
        accountRequest.setCurrency(Currency.RSD);


        updateAccountRequest = new UpdateAccountRequest();
        updateAccountRequest.setId(1L);
        updateAccountRequest.setUserId(1L);
        updateAccountRequest.setAccountNumber("1234512342423424234");
        updateAccountRequest.setAccountType(AccountType.SAVINGS);
        updateAccountRequest.setStatus(Status.ACTIVE);

        depositRequest = new DepositRequest();
        depositRequest.setAmount(new BigDecimal("123"));
        depositRequest.setUserId(1L);

        withdrawRequest = new WithdrawRequest();
        withdrawRequest.setAmount(new BigDecimal("123"));
        withdrawRequest.setUserId(1L);
    }


    @DisplayName("Create Account")
    @Test
    void testSaveAccount_whenValidDetailsProvided_returns201StatusCode() throws Exception {

        //given
        AccountDTO accountDTO = AccountDTO.builder()
                .accountType(accountRequest.getAccountType())
                .build();

        AccountResponse accountResponse = AccountResponse.builder()
                .accountType(accountRequest.getAccountType()).build();

        when(accountMapper.accountRequestToAccountDTO(accountRequest)).thenReturn(accountDTO);
        when(accountMapper.accountDTOToAccountResponse(accountDTO)).thenReturn(accountResponse);
        when(accountService.save(accountDTO)).thenReturn(accountDTO);

        RequestBuilder requestBuilder = post(AccountController.ACCOUNT_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountRequest));

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        AccountResponse accountResponse1 = objectMapper.readValue(response, AccountResponse.class);

        //then
        assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus(), getStatusMessage(201));
        assertNotNull(accountResponse1, "Account response should not be null.");
        assertEquals(accountResponse1.getAccountType(), accountRequest.getAccountType(), "AccountResponse should have same account type as expected");

        verify(accountMapper).accountRequestToAccountDTO(accountRequest);
        verify(accountMapper).accountDTOToAccountResponse(accountDTO);
        verify(accountService).save(accountDTO);
    }

    @DisplayName("Create Account Failed - Invalid Input")
    @Test
    void testSaveAccount_whenInvalidDetailsProvided_returns400StatusCode() throws Exception {

        //given
        accountRequest.setUserId(null);

        RequestBuilder requestBuilder = post(AccountController.ACCOUNT_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountRequest));


        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus(), getStatusMessage(400));
    }

    @DisplayName("Update Account")
    @Test
    void testUpdateAccount_whenValidDetailsProvided_returns200StatusCode() throws Exception {

        //given
        Timestamp timestamp = Timestamp.from(Instant.now());

        AccountDTO accountDTO = AccountDTO.builder()
                .id(updateAccountRequest.getId())
                .userId(updateAccountRequest.getUserId())
                .accountNumber(updateAccountRequest.getAccountNumber())
                .accountType(updateAccountRequest.getAccountType())
                .status(updateAccountRequest.getStatus())
                .userId(1L)
                .createdAt(timestamp)
                .updatedAt(timestamp)
                .build();

        AccountResponse accountResponse = AccountResponse.builder()
                .id(updateAccountRequest.getId())
                .accountNumber(updateAccountRequest.getAccountNumber())
                .accountType(updateAccountRequest.getAccountType())
                .status(updateAccountRequest.getStatus())
                .build();

        when(accountMapper.updateAccountRequestToAccountDTO(updateAccountRequest)).thenReturn(accountDTO);
        when(accountMapper.accountDTOToAccountResponse(accountDTO)).thenReturn(accountResponse);
        when(accountService.update(anyLong(), any(AccountDTO.class))).thenReturn(accountDTO);

        RequestBuilder requestBuilder = put(AccountController.ACCOUNT_URI_WITH_ID, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAccountRequest));


        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        AccountResponse accountResponse1 = objectMapper.readValue(response, AccountResponse.class);

        //then
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), getStatusMessage(200));
        assertNotNull(accountResponse1, "AccountResponse should not be null");
        assertAll("Check AccountResponse data",
                () -> assertEquals(updateAccountRequest.getId(), accountResponse1.getId(), "ID is not equal"),
                () -> assertEquals(updateAccountRequest.getAccountType(), accountResponse1.getAccountType(), "AccountType is not equal"),
                () -> assertEquals(updateAccountRequest.getAccountNumber(), accountResponse1.getAccountNumber(), "AccountNumber is not equal"),
                () -> assertEquals(updateAccountRequest.getStatus(), accountResponse1.getStatus(), "Status is not equal"));

        verify(accountMapper).updateAccountRequestToAccountDTO(updateAccountRequest);
        verify(accountMapper).accountDTOToAccountResponse(accountDTO);
        verify(accountService).update(anyLong(), any(AccountDTO.class));

    }

    @DisplayName("Update Account Failed - Invalid Input")
    @Test
    void testUpdateAccount_whenInvalidDetailsProvided_return400StatusCode() throws Exception {

        //given
        updateAccountRequest.setUserId(null);

        RequestBuilder requestBuilder = put(AccountController.ACCOUNT_URI_WITH_ID, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateAccountRequest));


        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();


        //then
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus(), getStatusMessage(400));
    }

    @DisplayName("Account Deposit")
    @Test
    void testDeposit_whenValidDetailsProvided_returns200StatusCode() throws Exception {

        //given
        Long accountId = 1L;
        DepositDTO depositDTO = new DepositDTO();
        depositDTO.setUserId(1L);
        depositDTO.setAmount(new BigDecimal("123"));

        TransactionDTO transactionDTO = TransactionDTO.builder().build();

        DepositResponse depositResponse = DepositResponse.builder()
                .balance(new BigDecimal("123"))
                .accountNumber("testNumber")
                .moneyInflow(depositDTO.getAmount())
                .build();

        when(transactionMapper.depositRequestToDepositDTO(depositRequest)).thenReturn(depositDTO);
        when(transactionMapper.transactionDTOToDepositResponse(transactionDTO)).thenReturn(depositResponse);
        when(accountService.deposit(accountId, depositDTO)).thenReturn(transactionDTO);

        RequestBuilder requestBuilder = put("/api/v1/account/deposit/{accountId}", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest));

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        DepositResponse depositResponse1 = objectMapper.readValue(response, DepositResponse.class);

        //then
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), getStatusMessage(200));
        assertNotNull(depositResponse1, "AccountResponse should not be null.");
        assertEquals(depositRequest.getAmount(), depositResponse1.getBalance(), "Balance is not equal.");

        verify(transactionMapper).depositRequestToDepositDTO(depositRequest);
        verify(transactionMapper).transactionDTOToDepositResponse(transactionDTO);
        verify(accountService).deposit(accountId, depositDTO);
    }

    @DisplayName("Account Deposit Failed - Invalid ID Provided")
    @Test
    void testDeposit_whenInvalidIdProvided_thenReturns404StatusCode() throws Exception {

        //given
        DepositDTO depositDTO = new DepositDTO();
        String accountNumber = "123515132412341";

        when(transactionMapper.depositRequestToDepositDTO(depositRequest)).thenReturn(depositDTO);
        when(accountService.deposit(anyLong(), any(DepositDTO.class))).thenThrow(AccountNotFoundException.class);

        RequestBuilder requestBuilder = put("/api/v1/account/deposit/{accountId}", accountNumber)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest));

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus(), getStatusMessage(404));

        verify(transactionMapper).depositRequestToDepositDTO(depositRequest);
        verify(accountService).deposit(anyLong(), any(DepositDTO.class));
    }

    @DisplayName("Account Deposit Failed - Invalid Details Provided")
    @Test
    void testDeposit_whenInvalidDetailsProvided_thenReturns400StatusCode() throws Exception {

        //given
        Long accountId = 1L;
        depositRequest.setAmount(null);
        TransactionDTO transactionDTO = TransactionDTO.builder().build();

        when(accountService.deposit(anyLong(), any(DepositDTO.class))).thenReturn(transactionDTO);

        RequestBuilder requestBuilder = put("/api/v1/account/deposit/{accountId}", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest));

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus(), getStatusMessage(400));

        verify(accountService, never()).deposit(anyLong(), any(DepositDTO.class));
    }

    @DisplayName("Account Deposit Failed - Request User ID doesn't match")
    @Test
    void testDeposit_whenUserIdDoesNotMatch_thenReturns409StatusCode() throws Exception {

        //given
        DepositDTO depositDTO = new DepositDTO();
        depositDTO.setUserId(1L);
        Long accountId = 1L;

        when(transactionMapper.depositRequestToDepositDTO(depositRequest)).thenReturn(depositDTO);
        when(accountService.deposit(anyLong(), any(DepositDTO.class))).thenThrow(UnauthorizedAccountAccessException.class);

        RequestBuilder requestBuilder = put("/api/v1/account/deposit/{accountId}", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest));

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        assertEquals(HttpStatus.CONFLICT.value(), mvcResult.getResponse().getStatus(), getStatusMessage(409));

        verify(transactionMapper).depositRequestToDepositDTO(depositRequest);
        verify(accountService).deposit(accountId, depositDTO);
    }

    @DisplayName("Account Deposit Failed - Account Status ")
    @ParameterizedTest(name = "Deposit attempt with status {0} should be denied")
    @EnumSource(value = Status.class, names = {"FROZEN", "BLOCKED", "RESTRICTED", "CLOSED"})
    void testDeposit_whenAccountStatusDoesNotAllow_returns409StatusCode(Status status) throws Exception {

        //given
        DepositDTO depositDTO = new DepositDTO();
        Long accountId = 1L;

        when(transactionMapper.depositRequestToDepositDTO(depositRequest)).thenReturn(depositDTO);
        when(accountService.deposit(anyLong(), any(DepositDTO.class))).thenThrow(getException(status));


        RequestBuilder requestBuilder = put("/api/v1/account/deposit/{accountId}", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.CONFLICT.value(), mvcResult.getResponse().getStatus(), getStatusMessage(409));

        verify(transactionMapper).depositRequestToDepositDTO(depositRequest);
        verify(accountService).deposit(anyLong(), any(DepositDTO.class));
    }

    @DisplayName("Account Withdraw")
    @Test
    void testWithdraw_whenValidDetailsProvided_returns200StatusCode() throws Exception {

        //given
        Long accountId = 1L;
        WithdrawDTO withdrawDTO = new WithdrawDTO();
        withdrawRequest.setAmount(new BigDecimal("50"));

        TransactionDTO transactionDTO = TransactionDTO.builder().build();

        WithdrawResponse withdrawResponse = WithdrawResponse.builder()
                .balance(new BigDecimal("50"))
                .build();

        when(transactionMapper.withdrawRequestToWithdrawDTO(withdrawRequest)).thenReturn(withdrawDTO);
        when(transactionMapper.transactionDTOToWithdrawResponse(transactionDTO)).thenReturn(withdrawResponse);
        when(accountService.withdraw(accountId, withdrawDTO)).thenReturn(transactionDTO);

        RequestBuilder requestBuilder = put("/api/v1/account/withdraw/{accountId}", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdrawRequest));

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        WithdrawResponse withdrawResponse1 = objectMapper.readValue(response, WithdrawResponse.class);

        //then
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), getStatusMessage(200));
        assertNotNull(withdrawResponse1, "AccountResponse should not be null.");
        assertEquals(withdrawRequest.getAmount(), withdrawResponse1.getBalance(), "Balance is not equal.");

        verify(transactionMapper).withdrawRequestToWithdrawDTO(withdrawRequest);
        verify(transactionMapper).transactionDTOToWithdrawResponse(transactionDTO);
        verify(accountService).withdraw(accountId, withdrawDTO);

    }

    @DisplayName("Account Withdraw Failed - Invalid ID Provided")
    @Test
    void testWithdraw_whenInvalidIdProvided_returns404StatusCode() throws Exception {

        //given
        WithdrawDTO withdrawDTO = new WithdrawDTO();
        Long accountId = 1L;

        when(transactionMapper.withdrawRequestToWithdrawDTO(withdrawRequest)).thenReturn(withdrawDTO);
        when(accountService.withdraw(anyLong(), any(WithdrawDTO.class))).thenThrow(AccountNotFoundException.class);

        RequestBuilder requestBuilder = put("/api/v1/account/withdraw/{accountId}", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdrawRequest));

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus(), getStatusMessage(404));

        verify(transactionMapper).withdrawRequestToWithdrawDTO(withdrawRequest);
        verify(accountService).withdraw(anyLong(), any(WithdrawDTO.class));
    }

    @Test
    void testWithdraw_whenInsufficientFoundReached_returns409StatusCode() throws Exception {

        //given
        WithdrawDTO withdrawDTO = new WithdrawDTO();
        Long accountId = 1L;

        when(transactionMapper.withdrawRequestToWithdrawDTO(withdrawRequest)).thenReturn(withdrawDTO);
        when(accountService.withdraw(anyLong(), any(WithdrawDTO.class))).thenThrow(InsufficientFundsException.class);

        RequestBuilder requestBuilder = put("/api/v1/account/withdraw/{accountId}", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdrawRequest));

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        assertEquals(HttpStatus.CONFLICT.value(), mvcResult.getResponse().getStatus(), getStatusMessage(409));

        verify(transactionMapper).withdrawRequestToWithdrawDTO(withdrawRequest);
        verify(accountService).withdraw(anyLong(), any(WithdrawDTO.class));
    }

    @DisplayName("Account Withdraw Failed - Invalid Details Provided")
    @Test
    void testWithdraw_whenInvalidDetailsProvided_returns400StatusCode() throws Exception {

        //given
        withdrawRequest.setAmount(null);

        Long accountId = 1L;
        TransactionDTO transactionDTO = TransactionDTO.builder().build();

        when(accountService.withdraw(anyLong(), any(WithdrawDTO.class))).thenReturn(transactionDTO);

        RequestBuilder requestBuilder = put("/api/v1/account/withdraw/{accountId}", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdrawRequest));

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus(), getStatusMessage(400));

        verify(accountService, never()).withdraw(anyLong(), any(WithdrawDTO.class));
    }

    @DisplayName("Account Withdraw Failed - Request User ID doesn't match")
    @Test
    void testWithdraw_whenUserIdDoesNotMatch_returns409StatusCode() throws Exception {

        //given
        WithdrawDTO withdrawDTO = new WithdrawDTO();
        withdrawDTO.setUserId(1L);
        Long accountId = 1L;

        when(transactionMapper.withdrawRequestToWithdrawDTO(withdrawRequest)).thenReturn(withdrawDTO);
        when(accountService.withdraw(accountId, withdrawDTO)).thenThrow(UnauthorizedAccountAccessException.class);

        RequestBuilder requestBuilder = put("/api/v1/account/withdraw/{accountId}", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdrawRequest));

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        assertEquals(HttpStatus.CONFLICT.value(), mvcResult.getResponse().getStatus(), getStatusMessage(409));

        verify(transactionMapper).withdrawRequestToWithdrawDTO(withdrawRequest);
        verify(accountService).withdraw(accountId, withdrawDTO);
    }

    @DisplayName("Account Withdraw Failed - Account Status ")
    @ParameterizedTest(name = "Withdraw attempt with status {0} should be denied")
    @EnumSource(value = Status.class, names = {"FROZEN", "BLOCKED", "RESTRICTED", "CLOSED"})
    void testWithdraw_whenAccountStatusDoesNotAllow_returns409StatusCode(Status status) throws Exception {

        //given
        WithdrawDTO withdrawDTO = new WithdrawDTO();
        Long accountId = 1L;

        when(transactionMapper.withdrawRequestToWithdrawDTO(withdrawRequest)).thenReturn(withdrawDTO);
        when(accountService.withdraw(anyLong(), any(WithdrawDTO.class))).thenThrow(getException(status));

        RequestBuilder requestBuilder = put("/api/v1/account/withdraw/{accountId}", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(withdrawRequest));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.CONFLICT.value(), mvcResult.getResponse().getStatus(), getStatusMessage(409));

        verify(transactionMapper).withdrawRequestToWithdrawDTO(withdrawRequest);
        verify(accountService).withdraw(anyLong(), any(WithdrawDTO.class));
    }

    @DisplayName("Account Transfer")
    @Test
    void testTransfer_whenValidDetailsProvided_returns200StatusCode() throws Exception {
        //given
        Long accountId = 1L;
        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setAmount(new BigDecimal("50"));

        TransactionDTO transactionDTO = TransactionDTO.builder().build();

        TransferResponse transferResponse = TransferResponse.builder()
                .userId(1L)
                .moneyOutflow(new BigDecimal("100"))
                .accountNumber("12314515665342645635564")
                .balance(new BigDecimal("100"))
                .status(TransactionStatus.SUCCESS)
                .build();

        TransferRequest transferRequest = TransferRequest.builder()
                .userId(1L)
                .recipientAccountNumber("12314515665342645635564")
                .amount(new BigDecimal("100"))
                .currency(Currency.RSD)
                .build();

        when(transactionMapper.transferRequestToTransferDTO(transferRequest)).thenReturn(transferDTO);
        when(transactionMapper.transactionDTOtoTransferResponse(transactionDTO)).thenReturn(transferResponse);
        when(accountService.transfer(accountId, transferDTO)).thenReturn(transactionDTO);

        RequestBuilder requestBuilder = put("/api/v1/account/transfer/{accountId}", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequest));

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        TransferResponse transferResponse1 = objectMapper.readValue(response, TransferResponse.class);

        //then
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), getStatusMessage(200));
        assertNotNull(transferResponse1, "AccountResponse should not be null.");
        assertEquals(transferRequest.getAmount(), transferResponse1.getBalance(), "Balance is not equal.");

        verify(transactionMapper).transferRequestToTransferDTO(transferRequest);
        verify(transactionMapper).transactionDTOtoTransferResponse(transactionDTO);
        verify(accountService).transfer(accountId, transferDTO);
    }

    @DisplayName("Account Transfer Failed - Invalid details provided")
    @Test
    void testTransfer_whenInvalidDetailsProvided_returns400StatusCode() throws Exception {
        //given
        Long accountId = 1L;
        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setAmount(new BigDecimal("50"));

        TransferRequest transferRequest = TransferRequest.builder()
                .userId(1L)
                .currency(Currency.RSD)
                .recipientAccountNumber("123145156")
                .amount(null)
                .build();

        when(transactionMapper.transferRequestToTransferDTO(transferRequest)).thenReturn(transferDTO);

        RequestBuilder requestBuilder = put("/api/v1/account/transfer/{accountId}", accountId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transferRequest));

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus(), getStatusMessage(400));

        verify(transactionMapper, never()).transferRequestToTransferDTO(transferRequest);
    }

    @DisplayName("Account Status Update")
    @Test
    void testUpdateStatus_whenValidDetailsProvided_returns200StatusCode() throws Exception {

        //given
        AccountStatusDTO accountStatusDTO = new AccountStatusDTO();
        accountStatusDTO.setStatus(Status.UPDATED);

        AccountDetailsDTO accountDetailsDTO = AccountDetailsDTO.builder().build();

        AccountStatusRequest accountStatusRequest = new AccountStatusRequest();
        accountStatusRequest.setStatus(Status.UPDATED);
        AccountResponse accountResponse = AccountResponse.builder().status(Status.UPDATED).build();

        when(accountMapper.accountStatusRequestToAccountStatusDTO(accountStatusRequest)).thenReturn(accountStatusDTO);
        when(accountMapper.accountDetailsDTOToAccountResponse(accountDetailsDTO)).thenReturn(accountResponse);
        when(accountService.setAccountStatus(1L, accountStatusDTO)).thenReturn(accountDetailsDTO);

        RequestBuilder requestBuilder = put("/api/v1/account/status/{accountId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountStatusRequest));

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        AccountResponse accountResponse1 = objectMapper.readValue(response, AccountResponse.class);

        //then
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), getStatusMessage(200));
        assertNotNull(accountResponse1, "AccountResponse should not be null.");
        assertEquals(Status.UPDATED, accountResponse1.getStatus(), "AccountResponse should have same status as request.");

        verify(accountMapper).accountStatusRequestToAccountStatusDTO(accountStatusRequest);
        verify(accountMapper).accountDetailsDTOToAccountResponse(accountDetailsDTO);
        verify(accountService).setAccountStatus(1L, accountStatusDTO);
    }

    @DisplayName("Account Status Update Failed - Invalid Details Provided")
    @Test
    void testUpdateStatus_whenInvalidDetailsProvided_returns400statusCode() throws Exception {

        //given
        AccountStatusRequest accountStatusRequest = new AccountStatusRequest();
        accountStatusRequest.setStatus(null);

        AccountStatusDTO accountStatusDTO = new AccountStatusDTO();
        accountStatusDTO.setStatus(null);

        AccountDetailsDTO accountDetailsDTO = AccountDetailsDTO.builder().build();


        when(accountService.setAccountStatus(1L, accountStatusDTO)).thenReturn(accountDetailsDTO);

        RequestBuilder requestBuilder = put("/api/v1/account/status/{accountId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountStatusRequest));

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus(), getStatusMessage(400));

        verify(accountService, never()).setAccountStatus(1L, accountStatusDTO);
    }

    @DisplayName("Account Status Update Failed - Invalid ID Provided")
    @Test
    void testUpdateStatus_whenInvalidIDProvided_returns404StatusCode() throws Exception {

        //given

        AccountStatusDTO accountStatusDTO = new AccountStatusDTO();
        accountStatusDTO.setStatus(Status.UPDATED);

        AccountStatusRequest accountStatusRequest = new AccountStatusRequest();
        accountStatusRequest.setStatus(Status.UPDATED);

        when(accountMapper.accountStatusRequestToAccountStatusDTO(accountStatusRequest)).thenReturn(accountStatusDTO);
        when(accountService.setAccountStatus(anyLong(), any(AccountStatusDTO.class))).thenThrow(AccountNotFoundException.class);

        RequestBuilder requestBuilder = put("/api/v1/account/status/{accountId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountStatusRequest));

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus(), getStatusMessage(404));

        verify(accountMapper).accountStatusRequestToAccountStatusDTO(accountStatusRequest);
        verify(accountService).setAccountStatus(anyLong(), any(AccountStatusDTO.class));
    }

    @DisplayName("Get All User Accounts")
    @Test
    void testGetAllUserAccounts_whenValidDetailsProvided_returns200StatusCode() throws Exception {

        //given
        List<AccountDTO> accountDTOList = List.of(mock(AccountDTO.class), mock(AccountDTO.class), mock(AccountDTO.class));
        when(accountService.getAllUserAccounts(anyLong())).thenReturn(accountDTOList);

        RequestBuilder requestBuilder = get("/api/v1/account/users/{userId}/accounts", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDTOList));

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        List<AccountDTO> list = objectMapper.readValue(response, new TypeReference<>() {
        });

        //then
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), getStatusMessage(200));
        assertNotNull(list, "List should not be null");
        assertEquals(3, list.size(), "List size doesn't match expected size");

        verify(accountService).getAllUserAccounts(anyLong());
    }

    @DisplayName("Delete Account")
    @Test
    void testDeleteAccount_whenValidIdProvided_thenCorrect() throws Exception {

        //given
        doNothing().when(accountService).delete(anyLong());

        RequestBuilder requestBuilder = delete(AccountController.ACCOUNT_URI_WITH_ID, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountRequest));

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();

        //then
        assertEquals(HttpStatus.NO_CONTENT.value(), mvcResult.getResponse().getStatus(), getStatusMessage(204));
    }

    @DisplayName("Delete Account Failed - Invalid ID Provided")
    @Test
    void testDeleteAccount_whenInvalidIDProvided_returns404StatusCode() throws Exception {

        doThrow(AccountNotFoundException.class).when(accountService).delete(anyLong());


        RequestBuilder requestBuilder = delete(AccountController.ACCOUNT_URI_WITH_ID, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountRequest));

        //when
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();


        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus(), getStatusMessage(404));

    }

    private static Class<? extends Throwable> getException(Status status) {
        return switch (status) {
            case FROZEN -> AccountFrozenException.class;
            case BLOCKED -> AccountBlockedException.class;
            case RESTRICTED -> AccountRestrictedException.class;
            case CLOSED -> AccountClosedException.class;
            default -> throw new IllegalStateException("Unexpected value: " + status);
        };
    }
}