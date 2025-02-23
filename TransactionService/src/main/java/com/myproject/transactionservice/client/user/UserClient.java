package com.myproject.transactionservice.client.user;

import com.myproject.transactionservice.client.response.ErrorResponse;
import com.myproject.transactionservice.exception.client.AccountErrorException;
import com.myproject.transactionservice.exception.client.ClientErrorException;
import com.myproject.transactionservice.exception.client.UserErrorException;
import com.myproject.transactionservice.exception.service.strategy.UserNotFoundException;
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
public class UserClient {

    public static final String BASE_URL = "/api/v1/user";
    public static final String CHECK_USER_URL = BASE_URL + "/check/{userId}";
    private final WebClient webClient;


    public UserClient(WebClient.Builder webClientBuilder, UserClientProperties properties) {
        log.info("User Service URL is {}", properties.getUrl());
        this.webClient = webClientBuilder.baseUrl(properties.getUrl()).clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                .responseTimeout(Duration.ofSeconds(5)))).build();
    }


    public String isUserExists(Long userId) {

        return executeRequest(CHECK_USER_URL, userId)
                .onErrorResume(UserErrorException.class, e -> Mono.error(new UserNotFoundException("User with ID " + userId + " doesn't exists.")))
                .onErrorResume(ClientErrorException.class, e -> Mono.error(new RuntimeException(e.getMessage())))
                .block();

    }

    private Mono<String> executeRequest(String url, Long userId) {

        return webClient.get()
                .uri(url, userId)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                        clientResponse -> clientResponse.bodyToMono(ErrorResponse.class)
                                .flatMap(errorResponse ->
                                        Mono.error(new UserErrorException(errorResponse))))
                .onStatus(HttpStatusCode::is4xxClientError, ClientUtils::handle4xxError)
                .onStatus(HttpStatusCode::is5xxServerError, ClientUtils::handle5xxError)
                .bodyToMono(String.class);

    }

}
