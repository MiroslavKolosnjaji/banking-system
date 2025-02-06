package com.myproject.accountservice.exception.controller;

/**
 * @author Miroslav Kološnjaji
 */
public class AccountStatusViolationException extends Exception{

    public AccountStatusViolationException() {
    }

    public AccountStatusViolationException(String message) {
        super(message);
    }

    public AccountStatusViolationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountStatusViolationException(Throwable cause) {
        super(cause);
    }

    public AccountStatusViolationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
