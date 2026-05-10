package com.payflow.payflow.core.dto.response;

import com.payflow.payflow.core.enums.PaymentStatus;

public class PaymentOrderResponse {

    private Long id;
    private Double amount;
    private String currency;
    private PaymentStatus status;

    public PaymentOrderResponse() {}

    public PaymentOrderResponse(Long id, Double amount, String currency, PaymentStatus status) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}