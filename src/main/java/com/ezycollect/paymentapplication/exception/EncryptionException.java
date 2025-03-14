package com.ezycollect.paymentapplication.exception;

public class EncryptionException extends RuntimeException {

    public EncryptionException(final String message, final Exception exception) {
        super(message, exception);
    }

}
