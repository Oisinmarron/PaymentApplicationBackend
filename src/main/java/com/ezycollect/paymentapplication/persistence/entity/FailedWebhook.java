package com.ezycollect.paymentapplication.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "FAILED_WEBHOOK")
public class FailedWebhook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FAILED_WEBHOOK_ID")
    private int failedWebhookId;
    @Column(name = "PAYMENT_ID")
    private int paymentId;
    @Column(name = "WEBHOOK_URL")
    private String webhookUrl;
    @Column(name = "WEBHOOK_EXCEPTION")
    private String webhookException;
    @Column(name = "FAILED_AT")
    private LocalDateTime failedAt;

    public FailedWebhook() {

    }

    public FailedWebhook(int paymentId, String webhookUrl, String webhookException, LocalDateTime failedAt) {
        this.paymentId = paymentId;
        this.webhookUrl = webhookUrl;
        this.webhookException = webhookException;
        this.failedAt = failedAt;
    }

    public int getFailedWebhookId() {
        return failedWebhookId;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public String getWebhookException() {
        return webhookException;
    }

    public LocalDateTime getFailedAt() {
        return failedAt;
    }
}
