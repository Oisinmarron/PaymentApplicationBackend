package com.ezycollect.paymentapplication.service;

import com.ezycollect.paymentapplication.component.ModelMapper;
import com.ezycollect.paymentapplication.exception.EncryptionException;
import com.ezycollect.paymentapplication.model.ApiResponse;
import com.ezycollect.paymentapplication.model.PaymentDetailsDto;
import com.ezycollect.paymentapplication.model.enums.EnumWebhookType;
import com.ezycollect.paymentapplication.persistence.dao.PaymentDao;
import com.ezycollect.paymentapplication.persistence.entity.PaymentDetails;
import com.ezycollect.paymentapplication.utils.AESUtil;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final WebhookService webhookService;
    private final PaymentDao paymentDao;
    private final ModelMapper modelMapper;
    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    public PaymentService(final WebhookService webhookService,
                          final PaymentDao paymentDao,
                          final ModelMapper modelMapper) {
        this.webhookService = webhookService;
        this.paymentDao = paymentDao;
        this.modelMapper = modelMapper;
    }

    public ApiResponse createPayment(final PaymentDetailsDto paymentDetailsDto) {

        log.debug("Encrypting card number of [{}]", paymentDetailsDto.getFirstName() + paymentDetailsDto.getLastName());

        paymentDetailsDto.setCardNumber(encryptCardNumber(paymentDetailsDto.getCardNumber(),
                paymentDetailsDto.getFirstName() + paymentDetailsDto.getLastName()));

        final PaymentDetails paymentDetails = modelMapper.createPaymentEntity(paymentDetailsDto);

        log.debug("Saving payment details");

        final PaymentDetails updatedPaymentDetails = paymentDao.savePaymentDetails(paymentDetails);

        webhookService.sendPaymentToWebhooks(paymentDetailsDto, updatedPaymentDetails.getPaymentId());

        return new ApiResponse(201, "Created payment for "
                + paymentDetailsDto.getFirstName() + " " + paymentDetailsDto.getLastName()
                + " and notified webhooks");
    }

    private String encryptCardNumber(final String cardNumber, final String userName) {

        try{
            return AESUtil.encrypt(cardNumber);
        } catch (Exception ex) {
            throw new EncryptionException("Error Encrypting CardNumber of - " + userName, ex);
        }
    }
}
