package com.ezycollect.paymentapplication.persistence.dao;

import com.ezycollect.paymentapplication.model.enums.EnumWebhookType;
import com.ezycollect.paymentapplication.persistence.entity.FailedWebhook;
import com.ezycollect.paymentapplication.persistence.entity.Webhook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@ActiveProfiles("integration-test")
@Testcontainers
public class WebhookDaoIT {

    @Autowired
    private WebhookDao webhookDao;

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test")
            .withUsername("root")
            .withPassword("Password_1");

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        // Dynamically set the DataSource properties to use the MySQL container
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @Test
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Save webhook url and type")
    public void test_saveWebhook() {

        Webhook webhook = new Webhook("https://webhook.site/8249ead2-2c15-4281-9610-81b3af4cb7b2", EnumWebhookType.NEW_PAYMENT);

        assertThat(webhook.getWebhookId(), is(0));

        webhookDao.saveWebhook(webhook);

        assertThat(webhook.getWebhookId(), is(2));
        assertThat(webhook.getUrl(), is("https://webhook.site/8249ead2-2c15-4281-9610-81b3af4cb7b2"));
        assertThat(webhook.getWebhookType(), is(EnumWebhookType.NEW_PAYMENT));
    }

    @Test
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Get webhook urls used when new payment details are added")
    public void test_getWebhookUrls() {

        List<String> webhooks = webhookDao.getWebhookUrls(EnumWebhookType.NEW_PAYMENT);

        assertThat(webhooks.size(), is(1));
        assertThat(webhooks.get(0), is("https://webhook.site/8249ead2-2c15-4281-9610-81b3af4cb7b2"));
    }

    @Test
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Save webhook failure data, for future scheduled retries")
    public void test_saveWebhookFailure() {

        FailedWebhook failedWebhook = new FailedWebhook(1, "https://webhook.site/8249ead2-2c15-4281-9610-81b364hdd", "ExceptionPlaceholder", LocalDateTime.of(2025,3,2,10,30,00));

        assertThat(failedWebhook.getFailedWebhookId(), is(0));

        webhookDao.saveWebhookFailure(failedWebhook);

        assertThat(failedWebhook.getFailedWebhookId(), is(2));
        assertThat(failedWebhook.getPaymentId(), is(1));
        assertThat(failedWebhook.getWebhookUrl(), is("https://webhook.site/8249ead2-2c15-4281-9610-81b364hdd"));
        assertThat(failedWebhook.getWebhookException(), is("ExceptionPlaceholder"));
        assertThat(failedWebhook.getFailedAt(), is(LocalDateTime.of(2025,3,2,10,30,00)));
    }

    @Test
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("Retrieve failed webhooks to retry")
    public void test_getFailedWebhooks() {

        List<FailedWebhook> failedWebhooks = webhookDao.getFailedWebhooks();

        assertThat(failedWebhooks.size(), is(1));
        assertThat(failedWebhooks.get(0).getFailedWebhookId(), is(1));
        assertThat(failedWebhooks.get(0).getPaymentId(), is(1));
        assertThat(failedWebhooks.get(0).getWebhookUrl(), is("https://webhook.site/8249ead2-2c15-4281-9610-81b3af4cb7b2"));
        assertThat(failedWebhooks.get(0).getWebhookException(), is("Exception123"));
        assertThat(failedWebhooks.get(0).getFailedAt(), is(LocalDateTime.of(2025,3,13,8,27,40)));
    }

    @Test
    @Sql(scripts = "/sql/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @DisplayName("After retries, remove failed webhook contact attempts")
    public void test_removeFailedWebhooks() {

        List<FailedWebhook> failedWebhooks = webhookDao.getFailedWebhooks();
        assertThat(failedWebhooks.size(), is(1));

        int deleteBoolean = webhookDao.removeFailedWebhooks(1);
        assertThat(deleteBoolean, is(1));

        failedWebhooks = webhookDao.getFailedWebhooks();
        assertThat(failedWebhooks.size(), is(0));
    }
}
