package com.myproject.transactionservice.exception.client;

import com.myproject.transactionservice.client.response.ErrorResponse;
import lombok.Getter;

/**
 * @author Miroslav Kološnjaji
 */
@Getter
public class AccountErrorException extends RuntimeException{

    private final ErrorResponse errorResponse;

    public AccountErrorException(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }

    public AccountErrorException(String message, ErrorResponse errorResponse) {
        super(message);
        this.errorResponse = errorResponse;
    }

    public AccountErrorException(String message, Throwable cause, ErrorResponse errorResponse) {
        super(message, cause);
        this.errorResponse = errorResponse;
    }

    public AccountErrorException(Throwable cause, ErrorResponse errorResponse) {
        super(cause);
        this.errorResponse = errorResponse;
    }

    public AccountErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, ErrorResponse errorResponse) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.errorResponse = errorResponse;
    }
}
