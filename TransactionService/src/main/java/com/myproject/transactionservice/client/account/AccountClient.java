package com.myproject.transactionservice.client.account;

import com.myproject.transactionservice.client.dto.DepositRequestDTO;
import com.myproject.transactionservice.client.dto.TransactionRequestDTO;
import com.myproject.transactionservice.client.dto.TransferRequestDTO;
import com.myproject.transactionservice.client.dto.WithdrawRequestDTO;
import com.myproject.transactionservice.client.response.AccountResponse;
import com.myproject.transactionservice.client.response.ErrorResponse;
import com.myproject.transactionservice.exception.client.ClientErrorException;
import com.myproject.transactionservice.exception.client.AccountErrorException;
import com.myproject.transactionservice.exception.client.ServerErrorException;
import com.myproject.transactionservice.util.ClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

/**
 * @author Miroslav Kološnjaji
 */
@Slf4j
@Component
public class AccountClient {

    public static final String BASE_URL = "/api/v1/account";
    public static final String DEPOSIT_URL = BASE_URL + "/deposit/{accountId}";
    public static final String WITHDRAW_URL = BASE_URL + "/withdraw/{accountId}";
    public static final String TRANSFER_URL = BASE_URL + "/transfer/{accountId}";

    private final WebClient webClient;

    public AccountClient(WebClient.Builder webclientBuilder, AccountClientProperties properties) {
        log.info("Account Service URL is: {}", properties.getUrl());
        this.webClient = webclientBuilder.baseUrl(properties.getUrl()).clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                .responseTimeout(Duration.ofSeconds(5)))).build();
    }

    public AccountResponse deposit(Long accountId, DepositRequestDTO depositRequestDTO) {
        return getResponse(DEPOSIT_URL, accountId, depositRequestDTO);
    }

    public AccountResponse withdraw(Long accountId, WithdrawRequestDTO withdrawRequestDTO) {
        return getResponse(WITHDRAW_URL, accountId, withdrawRequestDTO);
    }

    public AccountResponse transfer(Long accountId, TransferRequestDTO transferRequestDTO){
        return getResponse(TRANSFER_URL, accountId, transferRequestDTO);
    }

    private AccountResponse getResponse(String url, Long accountId, TransactionRequestDTO transactionRequestDTO) {

        try {

            return webClient.put().uri(url, accountId)
                    .bodyValue(transactionRequestDTO)
                    .retrieve()
                    .onStatus(HttpStatus.CONFLICT::equals,
                            clientResponse -> clientResponse.bodyToMono(ErrorResponse.class)
                                    .flatMap(errorResponse ->
                                            Mono.error(new AccountErrorException(errorResponse))))
                    .onStatus(HttpStatusCode::is4xxClientError, ClientUtils::handle4xxError)
                    .onStatus(HttpStatusCode::is5xxServerError, ClientUtils::handle5xxError)
                    .bodyToMono(AccountResponse.class)
                    .block();

        } catch (AccountErrorException e) {
            log.warn("Transaction failed due account restrictions: {}", e.getErrorResponse().getMessage());
            return AccountResponse.builder().description(e.getErrorResponse().getMessage()).build();
        }catch (ClientErrorException | ServerErrorException e){
            throw new RuntimeException(e.getMessage());
        }
    }

}
