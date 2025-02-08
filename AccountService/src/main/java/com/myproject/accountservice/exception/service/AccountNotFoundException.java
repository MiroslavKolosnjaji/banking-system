package com.myproject.accountservice.exception.service;

import com.myproject.accountservice.exception.controller.EntityNotFoundException;

/**
 * @author Miroslav Kološnjaji
 */
public class AccountNotFoundException extends EntityNotFoundException {

    public AccountNotFoundException() {
    }

    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountNotFoundException(Throwable cause) {
        super(cause);
    }

    public AccountNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
