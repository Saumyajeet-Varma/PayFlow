package com.payflow.payflow.dto.response;

import com.payflow.payflow.model.enums.PaymentStatus;

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

    public void setId(Long id) {
        this.id = id;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}