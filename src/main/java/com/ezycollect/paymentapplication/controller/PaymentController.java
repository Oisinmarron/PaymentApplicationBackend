package com.ezycollect.paymentapplication.controller;

import com.ezycollect.paymentapplication.model.ApiResponse;
import com.ezycollect.paymentapplication.model.PaymentDetailsDto;
import com.ezycollect.paymentapplication.model.WebhookDto;
import com.ezycollect.paymentapplication.service.PaymentService;
import com.ezycollect.paymentapplication.service.WebhookService;
import com.ezycollect.paymentapplication.utils.UrlValidator;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class PaymentController {

    private final WebhookService webhookService;
    private final PaymentService paymentService;
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    public PaymentController(final PaymentService paymentService,
                             final WebhookService webhookService) {
        this.paymentService = paymentService;
        this.webhookService = webhookService;
    }

    @PostMapping(value = "/api/create-payment", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> createPayment(@Valid @RequestBody final PaymentDetailsDto paymentDetailsDto) {

        return ResponseEntity
                .ok()
                .body(paymentService.createPayment(paymentDetailsDto));
    }

    @PostMapping(value = "/api/register-webhook", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> registerWebhook(@Valid @RequestBody final WebhookDto webhook) {

        log.debug("Checking if the provided webhook URL is valid");

        if (!UrlValidator.isValidUrl(webhook.getWebhookUrl())) {
            return ResponseEntity
                    .badRequest()
                    .body(new ApiResponse(400, "The provided webhook URL isn't an actual URL - "
                            + webhook.getWebhookUrl()));
        }

        return ResponseEntity
                .ok()
                .body(webhookService.addWebhook(webhook));
    }
}
