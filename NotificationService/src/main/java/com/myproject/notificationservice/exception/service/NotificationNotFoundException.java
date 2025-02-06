package com.myproject.notificationservice.exception.service;

/**
 * @author Miroslav Kološnjaji
 */
public class NotificationNotFoundException extends Exception{

    public NotificationNotFoundException() {
    }

    public NotificationNotFoundException(String message) {
        super(message);
    }

    public NotificationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotificationNotFoundException(Throwable cause) {
        super(cause);
    }

    public NotificationNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
