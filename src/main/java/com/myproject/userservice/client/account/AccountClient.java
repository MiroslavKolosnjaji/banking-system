package com.myproject.userservice.client.account;

import com.myproject.userservice.client.request.account.CreateAccountRequest;
import com.myproject.userservice.client.response.AccountResponse;
import com.myproject.userservice.util.ClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * @author Miroslav Kološnjaji
 */
@Slf4j
@Component
public class AccountClient {

    public static final String BASE_URL = "/api/v1/account";
    public static final String GET_ACCOUNTS_URL = BASE_URL + "/users/{userId}/accounts";


    private final WebClient webClient;

    public AccountClient(WebClient.Builder webClientBuilder, AccountClientProperties properties) {
        log.info("Account Service URL is {}", properties.getUrl());
        this.webClient = webClientBuilder.baseUrl(properties.getUrl()).build();

        log.info("Web client is {}", webClient);
    }

    public AccountResponse createAccount(CreateAccountRequest request) {
        return webClient.post().uri(BASE_URL)
                .bodyValue(request)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ClientUtils::handle4xxError)
                .onStatus(HttpStatusCode::is5xxServerError, ClientUtils::handle5xxError)
                .bodyToMono(AccountResponse.class)
                .block();
    }

    public List<AccountResponse> getUserAccounts(Long userId) {
        return webClient.get()
                .uri(GET_ACCOUNTS_URL, userId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, ClientUtils::handle4xxError)
                .onStatus(HttpStatusCode::is5xxServerError, ClientUtils::handle5xxError)
                .bodyToFlux(AccountResponse.class)
                .collectList()
                .block();
    }

}
