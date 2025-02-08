package com.myproject.notificationservice.exception.messaging.handler;

/**
 * @author Miroslav Kološnjaji
 */
public class NotRetryableException extends RuntimeException{

    public NotRetryableException() {
    }

    public NotRetryableException(String message) {
        super(message);
    }

    public NotRetryableException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotRetryableException(Throwable cause) {
        super(cause);
    }

    public NotRetryableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
