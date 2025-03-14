package com.ezycollect.paymentapplication.model;

import com.ezycollect.paymentapplication.model.enums.EnumWebhookType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class WebhookDto {
    @NotBlank(message = "Webhook Url cannot be blank")
    private String webhookUrl;
    @NotNull(message = "Webhook Type cannot be blank")
    private EnumWebhookType webhookType;

    public WebhookDto() {
    }

    public WebhookDto(String webhookUrl, EnumWebhookType webhookType) {
        this.webhookUrl = webhookUrl;
        this.webhookType = webhookType;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public EnumWebhookType getWebhookType() {
        return webhookType;
    }
}
