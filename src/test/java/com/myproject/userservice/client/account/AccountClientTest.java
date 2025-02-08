package com.myproject.userservice.client.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.userservice.client.request.account.CreateAccountRequest;
import com.myproject.userservice.client.response.AccountResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * @author Miroslav Kološnjaji
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

        WebClient.Builder webclientBuilder = WebClient.builder();
        String baseUrl = mockWebServer.url("/").toString();
        accountClient = new AccountClient(webclientBuilder, new AccountClientProperties(baseUrl));
    }

    @DisplayName("Create Account Request")
    @Test
    void testCreateAccount_whenValidDetailsProvided_returns200StatusCode() throws Exception {

        CreateAccountRequest createAccountRequest = CreateAccountRequest.builder()
                .userId(1L)
                .accountType("CHECKING")
                .build();

        AccountResponse accountResponse = AccountResponse.builder()
                .accountNumber("12345")
                .accountType("CHECKING")
                .userId(1L)
                .balance(new BigDecimal("0"))
                .status("ACTIVE")
                .createdAt(null)
                .updatedAt(null)
                .build();


        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setBody(objectMapper.writeValueAsString(accountResponse))
                .addHeader("Content-Type", "application/json"));

        AccountResponse response = accountClient.createAccount(createAccountRequest);

        assertNotNull(response, "Response should not be null.");
        assertEquals(accountResponse, response, "Response should be equal to expected response.");

        mockWebServer.takeRequest();
    }

    @DisplayName("Get All Accounts From User Request")
    @Test
    void testGetAllAccounts_whenListIsPopulated_returns200StatusCode() throws Exception {

        List<AccountResponse> responseList = List.of(mock(AccountResponse.class), mock(AccountResponse.class));

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setBody(objectMapper.writeValueAsString(responseList))
                .addHeader("Content-Type", "application/json"));

        List<AccountResponse> accounts = accountClient.getUserAccounts(1L);

        assertNotNull(accounts, "Response list should not be null.");
        assertEquals(2, accounts.size(), "Response list size should be 2.");

        mockWebServer.takeRequest();
    }

}