package com.myproject.userservice.client.account;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.myproject.userservice.client.request.account.CreateAccountRequest;
import com.myproject.userservice.client.response.AccountResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Miroslav Kološnjaji
 */
@WireMockTest(httpPort = 8090)
class AccountClientIT {

    AccountClient accountClient;

    WebClient.Builder webClientBuilder;


    @BeforeEach
    void setUp() {
        webClientBuilder = WebClient.builder();

        String BASE_URL = "http://localhost:8090";
        accountClient = new AccountClient(webClientBuilder, new AccountClientProperties(BASE_URL));
    }


    @DisplayName("Create Account Client Request")
    @Test
    void testCreateAccount_whenValidDetailsProvided_returns201StatusCode() {

        CreateAccountRequest createAccountRequest = new CreateAccountRequest();
        createAccountRequest.setAccountType("CHECKING");
        createAccountRequest.setUserId(1L);

        stubFor(post(urlPathEqualTo(AccountClient.BASE_URL))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("user-account.json")));

        AccountResponse accountResponse = accountClient.createAccount(createAccountRequest);


        assertNotNull(accountResponse);
        assertEquals(createAccountRequest.getAccountType(), accountResponse.getAccountType());

        verify(1, postRequestedFor(urlPathEqualTo(AccountClient.BASE_URL))
                .withRequestBody(matchingJsonPath("$.accountType", equalTo("CHECKING")))
                .withRequestBody(matchingJsonPath("$.userId", equalTo("1"))));
    }

    @DisplayName("Get All User Accounts Client Request")
    @Test
    void testGetAllUserAccounts_whenValidDetailsProvided_returns201StatusCode() {

        stubFor(get(urlPathMatching(AccountClient.BASE_URL + "/users/[0-9]/accounts"))
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("get-all-user-accounts.json")));

        List<AccountResponse> list = accountClient.getUserAccounts(1L);

        assertNotNull(list, "List should not be null");
        assertEquals(1, list.size(), "List should contain one element");

    }
}