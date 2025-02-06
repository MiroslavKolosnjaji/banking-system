package com.myproject.accountservice.exception.service;

/**
 * @author Miroslav Kološnjaji
 */
public class UnauthorizedAccountAccessException extends Exception {

    public UnauthorizedAccountAccessException() {
    }

    public UnauthorizedAccountAccessException(String message) {
        super(message);
    }

    public UnauthorizedAccountAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnauthorizedAccountAccessException(Throwable cause) {
        super(cause);
    }

    public UnauthorizedAccountAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
