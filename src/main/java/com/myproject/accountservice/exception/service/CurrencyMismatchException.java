package com.myproject.accountservice.exception.service;

import com.myproject.accountservice.exception.controller.AccountStatusViolationException;

/**
@author Miroslav Kološnjaji
*/
    public class CurrencyMismatchException extends AccountStatusViolationException {

    public CurrencyMismatchException() {
    }

    public CurrencyMismatchException(String message) {
        super(message);
    }

    public CurrencyMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public CurrencyMismatchException(Throwable cause) {
        super(cause);
    }

    public CurrencyMismatchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
