package com.myproject.accountservice.exception.service.status;

import com.myproject.accountservice.exception.controller.AccountStatusViolationException;

/**
 * @author Miroslav Kološnjaji
 */
public class AccountBlockedException extends AccountStatusViolationException {

    public AccountBlockedException() {
    }

    public AccountBlockedException(String message) {
        super(message);
    }

    public AccountBlockedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountBlockedException(Throwable cause) {
        super(cause);
    }

    public AccountBlockedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
