package com.ezycollect.paymentapplication.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.ezycollect.paymentapplication.component.ModelMapper;
import com.ezycollect.paymentapplication.exception.WebhookPaymentException;
import com.ezycollect.paymentapplication.model.PaymentDetailsDto;
import com.ezycollect.paymentapplication.model.enums.EnumWebhookType;
import com.ezycollect.paymentapplication.persistence.dao.PaymentDao;
import com.ezycollect.paymentapplication.persistence.dao.WebhookDao;
import com.ezycollect.paymentapplication.persistence.entity.FailedWebhook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class WebhookServiceTest {

    @Mock
    private WebhookDao webhookDao;
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private PaymentDao paymentDao;
    private WebhookService webhookService;
    private final String testUrl = "http://example.com/webhook";
    private final int paymentId = 123;
    private PaymentDetailsDto paymentDetailsDto;

    @BeforeEach
    void setup() {
        paymentDetailsDto = new PaymentDetailsDto();
        ModelMapper modelMapper = new ModelMapper();
        this.webhookService = new WebhookService(webhookDao, modelMapper, restTemplate, paymentDao);
    }

    @Test
    public void testCallWebhookSuccessOnFirstTry() {
        when(restTemplate.postForEntity(eq(testUrl), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("Success"));

        when(webhookDao.getWebhookUrls(EnumWebhookType.NEW_PAYMENT)).thenReturn(List.of(testUrl));

        webhookService.sendPaymentToWebhooks(paymentDetailsDto, paymentId);

        verify(restTemplate, times(1)).postForEntity(eq(testUrl), any(HttpEntity.class), eq(String.class));
        verify(webhookDao, times(1)).getWebhookUrls(any());
    }

    @Test
    public void testRetryFailedWebhooksRemovesAfterSuccess() {
        FailedWebhook failedWebhook = new FailedWebhook(paymentId, testUrl, "Previous failure", LocalDateTime.now());

        when(webhookDao.getFailedWebhooks()).thenReturn(List.of(failedWebhook));
        when(paymentDao.getPaymentDetailsById(paymentId)).thenReturn(paymentDetailsDto);
        when(restTemplate.postForEntity(eq(testUrl), any(HttpEntity.class), eq(String.class)))
                .thenReturn(ResponseEntity.ok("Success"));

        webhookService.retryFailedWebhooks();

        verify(webhookDao, times(1)).removeFailedWebhooks(anyInt());
    }

    @Test
    public void testRetryFailedWebhooksFailsAgain() {
        FailedWebhook failedWebhook = new FailedWebhook(paymentId, testUrl, "Previous failure", LocalDateTime.now());

        when(webhookDao.getFailedWebhooks()).thenReturn(List.of(failedWebhook));
        when(paymentDao.getPaymentDetailsById(paymentId)).thenReturn(paymentDetailsDto);

        doThrow(new HttpServerErrorException(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR))
                .when(restTemplate).postForEntity(eq(testUrl), any(HttpEntity.class), eq(String.class));

        assertThrows(WebhookPaymentException.class, () -> {
            webhookService.retryFailedWebhooks();
        });

        verify(webhookDao, never()).removeFailedWebhooks(anyInt());
    }
}
