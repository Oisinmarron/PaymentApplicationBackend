package com.ezycollect.paymentapplication.controller;

import com.ezycollect.paymentapplication.model.ApiResponse;
import com.ezycollect.paymentapplication.model.PaymentDetailsDto;
import com.ezycollect.paymentapplication.service.PaymentService;
import com.ezycollect.paymentapplication.service.WebhookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private WebhookService webhookServiceMock;
    @MockBean
    private PaymentService paymentServiceMock;
    @Captor
    private ArgumentCaptor<PaymentDetailsDto> paymentDetailsCaptor;

    @BeforeEach
    public void setup() {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void should_returnNewCustomerAnd200_afterTheCustomerHasBeenRegistered() throws Exception {

        ApiResponse apiResponse = new ApiResponse(201, "Created payment for Oisin Marron"
                + " and notified each webhook");

        when(paymentServiceMock.createPayment(any())).thenReturn(apiResponse);
        paymentDetailsCaptor = ArgumentCaptor.forClass(PaymentDetailsDto.class);

        mockMvc.perform(post("/api/create-payment")
                        .content(createCustomerJson(new PaymentDetailsDto("Oisin", "Marron", "NSW 2033", "1234567898765432")))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(201)))
                .andExpect(jsonPath("$.message", is("Created payment for Oisin Marron and notified each webhook")));

        verify(paymentServiceMock, times(1)).createPayment(paymentDetailsCaptor.capture());
        assertThat(paymentDetailsCaptor.getValue().getFirstName(), is("Oisin"));
        assertThat(paymentDetailsCaptor.getValue().getLastName(), is("Marron"));
        assertThat(paymentDetailsCaptor.getValue().getZipCode(), is("NSW 2033"));
        assertThat(paymentDetailsCaptor.getValue().getCardNumber(), is("1234567898765432"));
    }

    private String createCustomerJson(PaymentDetailsDto paymentDetailsDto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(paymentDetailsDto);
    }
}
