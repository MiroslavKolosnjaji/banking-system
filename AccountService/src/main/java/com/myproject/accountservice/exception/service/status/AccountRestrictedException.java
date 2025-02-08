package com.myproject.accountservice.exception.service.status;

import com.myproject.accountservice.exception.controller.AccountStatusViolationException;

/**
 * @author Miroslav Kološnjaji
 */
public class AccountRestrictedException extends AccountStatusViolationException {

    public AccountRestrictedException() {
    }

    public AccountRestrictedException(String message) {
        super(message);
    }

    public AccountRestrictedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AccountRestrictedException(Throwable cause) {
        super(cause);
    }

    public AccountRestrictedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
