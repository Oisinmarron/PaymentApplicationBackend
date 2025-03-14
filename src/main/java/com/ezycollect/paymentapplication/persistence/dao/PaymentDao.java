package com.ezycollect.paymentapplication.persistence.dao;

import com.ezycollect.paymentapplication.model.PaymentDetailsDto;
import com.ezycollect.paymentapplication.persistence.entity.PaymentDetails;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PaymentDao {

    @PersistenceContext
    private EntityManager entityManager;

    private static final String GET_PAYMENT_DETAILS_BY_ID_HQL =
            "SELECT new com.ezycollect.paymentapplication.model.PaymentDetailsDto(firstName, lastName, zipCode, " +
                    "cardNumber) FROM PaymentDetails WHERE paymentId = :paymentId";

    @Transactional
    public PaymentDetails savePaymentDetails(final PaymentDetails paymentDetails) {
        if (paymentDetails.getPaymentId() == 0) {
            entityManager.persist(paymentDetails);
            return paymentDetails;
        } else {
            return entityManager.merge(paymentDetails);
        }
    }

    @Transactional
    public PaymentDetailsDto getPaymentDetailsById(final int paymentId) {
        return entityManager.createQuery(GET_PAYMENT_DETAILS_BY_ID_HQL, PaymentDetailsDto.class)
                .setParameter("paymentId", paymentId)
                .getSingleResult();
    }
}
