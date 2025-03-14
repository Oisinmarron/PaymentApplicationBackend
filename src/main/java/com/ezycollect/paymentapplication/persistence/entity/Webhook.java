package com.ezycollect.paymentapplication.persistence.entity;

import com.ezycollect.paymentapplication.model.enums.EnumWebhookType;
import jakarta.persistence.*;

@Entity
@Table(name = "WEBHOOK")
public class Webhook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WEBHOOK_ID")
    private int webhookId;
    @Column(name = "URL")
    private String url;
    @Column(name = "WEBHOOK_TYPE")
    @Enumerated(EnumType.STRING)
    private EnumWebhookType webhookType;

    public Webhook() {

    }

    public Webhook(String url, EnumWebhookType webhookType) {
        this.url = url;
        this.webhookType = webhookType;
    }

    public int getWebhookId() {
        return webhookId;
    }

    public String getUrl() {
        return url;
    }

    public EnumWebhookType getWebhookType() {
        return webhookType;
    }
}
