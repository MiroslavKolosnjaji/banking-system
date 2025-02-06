package com.myproject.accountservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.accountservice.controller.request.*;
import com.myproject.accountservice.controller.response.AccountResponse;
import com.myproject.accountservice.dto.AccountDTO;
import com.myproject.accountservice.model.AccountType;
import com.myproject.accountservice.model.Currency;
import com.myproject.accountservice.model.Status;
import com.myproject.accountservice.service.AccountService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Miroslav Kološnjaji
 */
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    /**
     * accountService field is used only in transfer tests to retrieve account numbers
     * from the account service.
     */
    @Autowired
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private AccountRequest accountRequest;
    private AccountStatusRequest accountStatusRequest;
    private DepositRequest depositRequest;
    private WithdrawRequest withdrawRequest;

    private static Long accountId;
    private static String accountNumber;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        accountRequest = new AccountRequest();
        accountRequest.setAccountType(AccountType.CHECKING);
        accountRequest.setUserId(2L);
        accountRequest.setCurrency(Currency.RSD);

        depositRequest = new DepositRequest();
        depositRequest.setAmount(new BigDecimal("100"));
        depositRequest.setUserId(2L);

        withdrawRequest = new WithdrawRequest();
        withdrawRequest.setAmount(new BigDecimal("100"));
        withdrawRequest.setUserId(2L);

        accountStatusRequest = new AccountStatusRequest();
        accountStatusRequest.setStatus(Status.UPDATED);
    }

    @DisplayName("Create Account")
    @Order(1)
    @Test
    void testCreateAccount_whenValidInputProvided_return201StatusCode() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post(AccountController.ACCOUNT_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", AccountController.ACCOUNT_URI + "/" + 2))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountType").value(AccountType.CHECKING.name()))
                .andExpect(jsonPath("$.status").value(Status.ACTIVE.name()))
                .andReturn();


        String response = mvcResult.getResponse().getContentAsString();
        AccountResponse accountResponse = objectMapper.readValue(response, AccountResponse.class);

        accountId = accountResponse.getId();
        accountNumber = accountResponse.getAccountNumber();

        assertNotNull(accountId, "Account Number should be generated");
    }

    @DisplayName("Create Account Failed - Invalid Input Provided")
    @Test
    void testCreateAccountWhenInvalidInputProvided_returns400StatusCode() throws Exception {

        accountRequest.setUserId(null);

        mockMvc.perform(post(AccountController.ACCOUNT_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Update Account")
    @Order(2)
    @Test
    void testUpdateAccount_whenValidDetailsProvided_returns200StatusCode() throws Exception {

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                .id(accountId)
                .accountNumber(accountNumber)
                .userId(2L)
                .accountType(AccountType.SAVINGS)
                .status(Status.ACTIVE)
                .build();

        mockMvc.perform(put(AccountController.ACCOUNT_URI_WITH_ID, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAccountRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountType").value("SAVINGS"));
    }

    @DisplayName("Update Account Failed - Invalid Input Provided")
    @Test
    void testUpdateAccount_whenInvalidInputProvided_returns400StatusCode() throws Exception {

        accountRequest.setUserId(null);

        mockMvc.perform(put(AccountController.ACCOUNT_URI_WITH_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Update Account Failed - Invalid ID provided")
    @Test
    void testUpdateAccount_whenInvalidIDProvided_returns404StatusCode() throws Exception {

        UpdateAccountRequest updateAccountRequest = UpdateAccountRequest.builder()
                .id(accountId)
                .accountNumber(accountNumber)
                .userId(2L)
                .accountType(AccountType.SAVINGS)
                .status(Status.ACTIVE)
                .build();

        mockMvc.perform(put(AccountController.ACCOUNT_URI_WITH_ID, 99L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAccountRequest)))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Account Deposit")
    @Order(3)
    @Test
    void testDeposit_whenValidInputProvided_returns200StatusCode() throws Exception {

        depositRequest.setAmount(new BigDecimal("200"));

        mockMvc.perform(put("/api/v1/account/deposit/{accountId}", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isOk());
    }

    @DisplayName("Account Deposit Failed - Invalid Input Provided")
    @Order(4)
    @Test
    void testDeposit_whenInvalidInputProvided_returns400StatusCode() throws Exception {

        depositRequest.setUserId(null);

        mockMvc.perform(put("/api/v1/account/deposit/{accountId}", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Account Withdraw")
    @Order(6)
    @Test
    void testWithdraw_whenValidInputProvided_returns200StatusCode() throws Exception {

        withdrawRequest.setAmount(new BigDecimal("50"));

        mockMvc.perform(put("/api/v1/account/withdraw/{accountId}", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isOk());
    }

    @DisplayName("Account Withdraw Failed - Invalid Input Provided")
    @Order(7)
    @Test
    void testWithdraw_whenInvalidInputProvided_returns400StatusCode() throws Exception {

        withdrawRequest.setUserId(null);

        mockMvc.perform(put("/api/v1/account/withdraw/{accountId}", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Account Transfer")
    @Order(9)
    @Test
    void testTransfer_whenValidInputProvided_returns200StatusCode() throws Exception {

        AccountDTO senderAccount = accountService.getAllUserAccounts(2L).get(0);
        BigDecimal initialBalance = senderAccount.getBalance();

        AccountDTO recipientAccount = accountService.getAllUserAccounts(1L).get(0);

        TransferRequest transferRequest = TransferRequest.builder()
                .userId(2L)
                .recipientAccountNumber(recipientAccount.getAccountNumber())
                .amount(new BigDecimal("100.0"))
                .currency(Currency.RSD)
                .build();

        mockMvc.perform(put("/api/v1/account/transfer/{accountId}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value("50.0"))
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        assertEquals(new BigDecimal("150.00"), initialBalance);
    }

    @DisplayName("Account Transfer Failed - Invalid Input Provided")
    @Order(10)
    @Test
    void testTransfer_whenInvalidInputProvided_returns400StatusCode() throws Exception {


        TransferRequest transferRequest = TransferRequest.builder()
                .userId(2L)
                .recipientAccountNumber(null)
                .amount(new BigDecimal("100.0"))
                .currency(Currency.RSD)
                .build();

        mockMvc.perform(put("/api/v1/account/transfer/{accountId}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isBadRequest());


    }

    @DisplayName("Update Account Status")
    @Order(8)
    @Test
    void testUpdateStatus_whenValidInputProvided_returns200StatusCode() throws Exception {

        accountStatusRequest.setStatus(Status.ACTIVE);

        mockMvc.perform(put("/api/v1/account/status/{accountId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountStatusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @DisplayName("Update Account Status Failed - Invalid Input Provided")
    @Test
    void testUpdateAccountStatus_whenInvalidDetailsProvided_returns400StatusCode() throws Exception {

        accountStatusRequest.setStatus(null);

        mockMvc.perform(put("/api/v1/account/status/{accountId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountStatusRequest)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Get User Accounts")
    @Order(5)
    @Test
    void testGetUserAccounts_whenValidIDProvided_returns200StatusCode() throws Exception {

        mockMvc.perform(get("/api/v1/account/users/{userId}/accounts", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @DisplayName("Delete Account")
    @Order(99)
    @Test
    void testDeleteAccount_whenValidIdProvided_returns204StatusCode() throws Exception {

        mockMvc.perform(delete(AccountController.ACCOUNT_URI_WITH_ID, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accountRequest)))
                .andExpect(status().isNoContent());
    }
}