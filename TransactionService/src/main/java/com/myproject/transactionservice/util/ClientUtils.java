package com.myproject.transactionservice.util;

import com.myproject.transactionservice.exception.client.ClientErrorException;
import com.myproject.transactionservice.exception.client.ServerErrorException;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

/**
 * @author Miroslav Kološnjaji
 */
public class ClientUtils {

    public static Mono<? extends Throwable> handle4xxError(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(String.class).flatMap(errorBody ->
                Mono.error(new ClientErrorException(errorBody)));
    }

    public static Mono<? extends Throwable> handle5xxError(ClientResponse clientResponse) {
        return clientResponse.bodyToMono(String.class).flatMap(errorBody ->
                Mono.error(new ServerErrorException(errorBody)));
    }
}
