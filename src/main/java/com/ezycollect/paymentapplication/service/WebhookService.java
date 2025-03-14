package com.ezycollect.paymentapplication.service;

import com.ezycollect.paymentapplication.component.ModelMapper;
import com.ezycollect.paymentapplication.exception.WebhookPaymentException;
import com.ezycollect.paymentapplication.model.ApiResponse;
import com.ezycollect.paymentapplication.model.PaymentDetailsDto;
import com.ezycollect.paymentapplication.model.WebhookDto;
import com.ezycollect.paymentapplication.model.enums.EnumWebhookType;
import com.ezycollect.paymentapplication.persistence.dao.PaymentDao;
import com.ezycollect.paymentapplication.persistence.dao.WebhookDao;
import com.ezycollect.paymentapplication.persistence.entity.FailedWebhook;
import com.ezycollect.paymentapplication.persistence.entity.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WebhookService {

    private final WebhookDao webhookDao;
    private final ModelMapper modelMapper;
    private final RestTemplate restTemplate;
    private final PaymentDao paymentDao;
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    public WebhookService(final WebhookDao webhookDao,
                          final ModelMapper modelMapper,
                          final RestTemplate restTemplate,
                          final PaymentDao paymentDao) {
        this.webhookDao = webhookDao;
        this.modelMapper = modelMapper;
        this.restTemplate = restTemplate;
        this.paymentDao = paymentDao;
    }

    public ApiResponse addWebhook(final WebhookDto webhook) {

        log.debug("Converting webhook dto object to entity");

        final Webhook webhookEntity = modelMapper.createWebhookEntity(webhook);

        log.info("Saving webhook with URL [{}] to database", webhook.getWebhookUrl());

        webhookDao.saveWebhook(webhookEntity);

        return new ApiResponse(201, "Webhook with URL " + webhook.getWebhookUrl()
                + " was added successfully");
    }

    public void sendPaymentToWebhooks(final PaymentDetailsDto paymentDetailsDto, final int paymentId) {

        final List<String> webhookUrls = webhookDao.getWebhookUrls(EnumWebhookType.NEW_PAYMENT);

        log.info("Sending payment details to [{}] webhooks", webhookUrls.size());

        webhookUrls.forEach(webhookUrl ->
        {
            try {

                callWebhook(webhookUrl, paymentDetailsDto, paymentId);

            } catch (WebhookPaymentException ex) {

                // Don't throw as we want to continue with other webhooks, exception stored in database

                log.error("Webhook at [{}] could not be sent payment details", webhookUrl);

                final FailedWebhook failedWebhook =
                        new FailedWebhook(paymentId, webhookUrl, ex.getMessage(), LocalDateTime.now());

                webhookDao.saveWebhookFailure(failedWebhook);

                log.info("Saved failed webhook to retry table - [{}]", webhookUrl);
            }
        });
    }

    private void callWebhook(final String webhookUrl, final PaymentDetailsDto paymentDetailsDto, final int paymentId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        final HttpEntity<PaymentDetailsDto> request = new HttpEntity<>(paymentDetailsDto, headers);

        try {

            restTemplate.postForEntity(webhookUrl, request, String.class);

        } catch (Exception ex) {

            throw new WebhookPaymentException("Webhook at " + webhookUrl + " failed again", ex);

        }
    }

    @Scheduled(fixedRate = 300000)
    public void retryFailedWebhooks() {
        final List<FailedWebhook> failedWebhooks = webhookDao.getFailedWebhooks();

        for (FailedWebhook failedWebhook : failedWebhooks) {
            try {
                // Retrying webhook
                callWebhook(failedWebhook.getWebhookUrl(),
                        paymentDao.getPaymentDetailsById(failedWebhook.getPaymentId()),
                        failedWebhook.getPaymentId());

                // Remove from DB after success
                webhookDao.removeFailedWebhooks(failedWebhook.getFailedWebhookId());

                log.debug("Removed failed webhook of url [{}] after the payment details were sent correctly " +
                        "after retry", failedWebhook.getWebhookUrl());

            } catch (Exception ex) {
                throw new WebhookPaymentException("Webhook at " + failedWebhook.getWebhookUrl() + " failed again", ex);
            }
        }
    }
}
