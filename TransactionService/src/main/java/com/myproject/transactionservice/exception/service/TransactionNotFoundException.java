package com.myproject.transactionservice.exception.service;

import com.myproject.transactionservice.exception.controller.EntityNotFoundException;

/**
 * @author Miroslav Kološnjaji
 */
public class TransactionNotFoundException extends EntityNotFoundException {

    public TransactionNotFoundException() {
    }

    public TransactionNotFoundException(String message) {
        super(message);
    }

    public TransactionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public TransactionNotFoundException(Throwable cause) {
        super(cause);
    }

    public TransactionNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
