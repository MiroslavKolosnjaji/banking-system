package com.myproject.accountservice.exception.service.status;

import com.myproject.accountservice.exception.controller.AccountStatusViolationException;

/**
 * @author Miroslav Kološnjaji
 */
public class AccountClosedException extends AccountStatusViolationException {
    public AccountClosedException() {
    }

    public AccountClosedException(String message) {
        super(message);
    }

    public AccountClosedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountClosedException(Throwable cause) {
        super(cause);
    }

    public AccountClosedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
