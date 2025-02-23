package com.myproject.userservice.exception.service;

import com.myproject.userservice.exception.controller.EntityNotFoundException;

/**
 * @author Miroslav Kološnjaji
 */
public class EmailNotFoundException extends EntityNotFoundException {

    public EmailNotFoundException() {
    }

    public EmailNotFoundException(String message) {
        super(message);
    }

    public EmailNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmailNotFoundException(Throwable cause) {
        super(cause);
    }

    public EmailNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
