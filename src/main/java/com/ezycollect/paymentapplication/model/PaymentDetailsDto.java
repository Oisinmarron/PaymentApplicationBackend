package com.ezycollect.paymentapplication.model;

import jakarta.validation.constraints.NotBlank;

public class PaymentDetailsDto {
    @NotBlank(message = "First name cannot be blank")
    private String firstName;
    @NotBlank(message = "Last name cannot be blank")
    private String lastName;
    @NotBlank(message = "Zip code cannot be blank")
    private String zipCode;
    @NotBlank(message = "Card number cannot be blank")
    private String cardNumber;

    public PaymentDetailsDto() {
    }

    public PaymentDetailsDto(String firstName, String lastName, String zipCode, String cardNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.zipCode = zipCode;
        this.cardNumber = cardNumber;
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

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
