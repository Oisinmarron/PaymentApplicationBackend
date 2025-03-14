package com.ezycollect.paymentapplication.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "PAYMENT_DETAILS")
public class PaymentDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PAYMENT_ID")
    private int paymentId;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "SECOND_NAME")
    private String lastName;
    @Column(name = "ZIP_CODE")
    private String zipCode;
    @Column(name = "CARD_NUMBER")
    private String cardNumber;

    public PaymentDetails() {

    }

    public PaymentDetails(String firstName, String lastName, String zipCode, String cardNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.zipCode = zipCode;
        this.cardNumber = cardNumber;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCardNumber() {
        return cardNumber;
    }
}
