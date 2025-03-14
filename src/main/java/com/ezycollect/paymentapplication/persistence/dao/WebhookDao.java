package com.ezycollect.paymentapplication.persistence.dao;

import com.ezycollect.paymentapplication.model.enums.EnumWebhookType;
import com.ezycollect.paymentapplication.persistence.entity.FailedWebhook;
import com.ezycollect.paymentapplication.persistence.entity.Webhook;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WebhookDao {

    @PersistenceContext
    private EntityManager entityManager;

    private static final String GET_WEBHOOK_URLS_HQL =
            "SELECT url FROM Webhook WHERE webhookType = :webhookType";

    private static final String GET_FAILED_WEBHOOKS_HQL =
            "FROM FailedWebhook ORDER BY failedAt asc";

    private static final String DELETE_FAILED_WEBHOOK_WHERE_ID_HQL =
            "DELETE FROM FailedWebhook WHERE failedWebhookId = :failedWebhookId";


    @Transactional
    public Webhook saveWebhook(final Webhook webhook) {
        if (webhook.getWebhookId() == 0) {
            entityManager.persist(webhook);
            return webhook;
        } else {
            return entityManager.merge(webhook);
        }
    }

    @Transactional
    public List<String> getWebhookUrls(final EnumWebhookType webhookType) {
        return entityManager.createQuery(GET_WEBHOOK_URLS_HQL, String.class)
                .setParameter("webhookType", webhookType)
                .getResultList();
    }

    @Transactional
    public FailedWebhook saveWebhookFailure(final FailedWebhook failedWebhook) {
        if (failedWebhook.getFailedWebhookId() == 0) {
            entityManager.persist(failedWebhook);
            return failedWebhook;
        } else {
            return entityManager.merge(failedWebhook);
        }
    }

    @Transactional
    public List<FailedWebhook> getFailedWebhooks() {
        return entityManager.createQuery(GET_FAILED_WEBHOOKS_HQL, FailedWebhook.class)
                .getResultList();
    }

    @Transactional
    public int removeFailedWebhooks(final int failedWebhookId) {

        return entityManager.createQuery(DELETE_FAILED_WEBHOOK_WHERE_ID_HQL)
                .setParameter("failedWebhookId", failedWebhookId)
                .executeUpdate();
    }
}
