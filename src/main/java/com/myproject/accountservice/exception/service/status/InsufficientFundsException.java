package com.myproject.accountservice.exception.service.status;

import com.myproject.accountservice.exception.controller.AccountStatusViolationException;

/**
 * @author Miroslav Kološnjaji
 */
public class InsufficientFundsException extends AccountStatusViolationException {

    public InsufficientFundsException() {
    }

    public InsufficientFundsException(String message) {
        super(message);
    }

    public InsufficientFundsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InsufficientFundsException(Throwable cause) {
        super(cause);
    }

    public InsufficientFundsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
