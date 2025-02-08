package com.myproject.transactionservice.client;

import com.myproject.transactionservice.client.account.AccountClient;
import com.myproject.transactionservice.client.account.AccountClientProperties;
import com.myproject.transactionservice.client.dto.DepositRequestDTO;
import com.myproject.transactionservice.client.dto.WithdrawRequestDTO;
import com.myproject.transactionservice.client.response.AccountResponse;
import com.myproject.transactionservice.client.response.ErrorResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
/**
@author Miroslav Kološnjaji
*/
    class AccountClientTest {

        private MockWebServer mockWebServer;
        private AccountClient accountClient;
        private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        objectMapper = new ObjectMapper();

        WebClient.Builder webcientBuilder = WebClient.builder();
        String baseUrl = mockWebServer.url("/").toString();
        accountClient = new AccountClient(webcientBuilder, new AccountClientProperties(baseUrl));
    }


    @DisplayName("Deposit")
    @Test
    void testDepositAccount_whenValidDetailsProvided_returns200StatusCode() throws Exception {

        DepositRequestDTO depositRequest = new DepositRequestDTO();
        depositRequest.setUserId(1L);
        depositRequest.setAmount(new BigDecimal("100"));

        AccountResponse accountResponse = AccountResponse.builder()
                .accountNumber("1234***56")
                .description("Transaction is successfully performed")
                .status("Successful")
                .balance(new BigDecimal("100"))
                .build();

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setBody(objectMapper.writeValueAsString(accountResponse))
                .addHeader("Content-Type", "application/json"));


        AccountResponse response = accountClient.deposit(1L, depositRequest);

        assertAll("Checking response fields",
                () -> assertEquals(accountResponse.getAccountNumber(), response.getAccountNumber(), "Account number doesn't match expected account number."),
                () -> assertEquals(accountResponse.getDescription(), response.getDescription(), "Description doesn't match expected string."),
                () -> assertEquals(accountResponse.getStatus(), response.getStatus(), "Status doesn't match expected string."),
                () -> assertEquals(accountResponse.getBalance(), response.getBalance(), "Balance doesn't match expected value."));

        mockWebServer.takeRequest();
    }

    @DisplayName("Deposit - when respond is 409 error")
    @Test
    void testDepositAccount_when409ErrorRespond_thenCatchAccountErrorException() throws Exception {

        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("Test");
        errorResponse.setTimestamp(Instant.now().toString());

        DepositRequestDTO depositRequest = new DepositRequestDTO();
        depositRequest.setUserId(1L);
        depositRequest.setAmount(new BigDecimal("100"));


        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(409)
                .setBody(objectMapper.writeValueAsString(errorResponse))
                .addHeader("Content-Type", "application/json"));

        AccountResponse response = accountClient.deposit(1L, depositRequest);

        assertNotNull(response, "Response should not be null.");
        assertEquals(errorResponse.getMessage(), response.getDescription(), "Description doesn't match expected string.");

        mockWebServer.takeRequest();
    }

    @DisplayName("Deposit - when respond is 4xx error")
    @Test
    void testDepositAccount_when4xxErrorRespond_thenCatchClientErrorException() throws Exception {

        String errorMessage = "TEST MESSAGE";

        DepositRequestDTO depositRequest = new DepositRequestDTO();
        depositRequest.setUserId(1L);
        depositRequest.setAmount(new BigDecimal("100"));


        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody(errorMessage)
                .addHeader("Content-Type", "application/json"));

        Executable executable = () -> accountClient.deposit(1L, depositRequest);

        assertThrows(RuntimeException.class, executable, "Expected RuntimeException.");

        mockWebServer.takeRequest();
   }

    @DisplayName("Deposit - when respond is 5xx error")
    @Test
    void testDepositAccount_when5xxErrorRespond_thenCatchClientErrorException() throws Exception {

        String errorMessage = "TEST MESSAGE";

        DepositRequestDTO depositRequest = new DepositRequestDTO();
        depositRequest.setUserId(1L);
        depositRequest.setAmount(new BigDecimal("100"));


        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody(errorMessage)
                .addHeader("Content-Type", "application/json"));

        Executable executable = () -> accountClient.deposit(1L, depositRequest);

        assertThrows(RuntimeException.class, executable, "Expected RuntimeException.");

        mockWebServer.takeRequest();
    }

    @DisplayName("Withdraw")
    @Test
    void testWithdrawAccount_whenValidDetailsProvided_returns200StatusCode() throws Exception {

        WithdrawRequestDTO withdrawRequest = new WithdrawRequestDTO();
        withdrawRequest.setUserId(1L);
        withdrawRequest.setAmount(new BigDecimal("100"));

        AccountResponse accountResponse = AccountResponse.builder()
                .accountNumber("1234***56")
                .description("Transaction is successfully performed")
                .status("Successful")
                .balance(new BigDecimal("100"))
                .build();

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setBody(objectMapper.writeValueAsString(accountResponse))
                .addHeader("Content-Type", "application/json"));


        AccountResponse response = accountClient.withdraw(1L, withdrawRequest);

        assertAll("Checking response fields",
                () -> assertEquals(accountResponse.getAccountNumber(), response.getAccountNumber(), "Account number doesn't match expected account number."),
                () -> assertEquals(accountResponse.getDescription(), response.getDescription(), "Description doesn't match expected string."),
                () -> assertEquals(accountResponse.getStatus(), response.getStatus(), "Status doesn't match expected string."),
                () -> assertEquals(accountResponse.getBalance(), response.getBalance(), "Balance doesn't match expected value."));

        mockWebServer.takeRequest();
    }
}