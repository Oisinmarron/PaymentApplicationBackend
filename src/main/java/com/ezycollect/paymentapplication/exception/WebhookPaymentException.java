package com.ezycollect.paymentapplication.exception;

public class WebhookPaymentException extends RuntimeException {

    public WebhookPaymentException(final String message, final Exception exception) {
        super(message, exception);
    }

}
