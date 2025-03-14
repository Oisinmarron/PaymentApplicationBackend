package com.ezycollect.paymentapplication.advice;

import com.ezycollect.paymentapplication.exception.EncryptionException;
import com.ezycollect.paymentapplication.exception.WebhookPaymentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public GlobalExceptionHandler() {
    }

    @ExceptionHandler(WebhookPaymentException.class)
    public ResponseEntity handleWebhookPaymentDeliveryError(final WebhookPaymentException exception) {
        log.error("Could not send payment details to webhook url", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Collections.singletonMap("message", "Could not send payment details to webhook url"));
    }

    @ExceptionHandler(EncryptionException.class)
    public ResponseEntity cardNumberEncyrptionError(final EncryptionException exception) {
        log.error("Issue encrypting a new card number", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                Collections.singletonMap("message", exception.getMessage()));
    }
}
