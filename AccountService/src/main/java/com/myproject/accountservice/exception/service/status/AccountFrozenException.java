package com.myproject.accountservice.exception.service.status;

import com.myproject.accountservice.exception.controller.AccountStatusViolationException;

/**
 * @author Miroslav Kološnjaji
 */
public class AccountFrozenException extends AccountStatusViolationException {

    public AccountFrozenException() {
    }

    public AccountFrozenException(String message) {
        super(message);
    }

    public AccountFrozenException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountFrozenException(Throwable cause) {
        super(cause);
    }

    public AccountFrozenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
