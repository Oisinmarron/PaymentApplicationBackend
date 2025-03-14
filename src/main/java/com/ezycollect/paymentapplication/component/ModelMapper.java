package com.ezycollect.paymentapplication.component;

import com.ezycollect.paymentapplication.model.PaymentDetailsDto;
import com.ezycollect.paymentapplication.model.WebhookDto;
import com.ezycollect.paymentapplication.persistence.entity.PaymentDetails;
import com.ezycollect.paymentapplication.persistence.entity.Webhook;
import org.springframework.stereotype.Component;

@Component
public class ModelMapper {

    public PaymentDetails createPaymentEntity(final PaymentDetailsDto paymentDetailsDto) {

        return new PaymentDetails(
                paymentDetailsDto.getFirstName(),
                paymentDetailsDto.getLastName(),
                paymentDetailsDto.getZipCode(),
                paymentDetailsDto.getCardNumber()
        );
    }

    public Webhook createWebhookEntity(final WebhookDto webhook) {

        return new Webhook(webhook.getWebhookUrl(), webhook.getWebhookType());
    }
}
