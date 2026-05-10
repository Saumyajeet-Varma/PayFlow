package com.payflow.payflow.model.entity;

import com.payflow.payflow.model.enums.PaymentStatus;
import jakarta.persistence.*;

@Entity
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    private String currency;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    public PaymentOrder() {
    }

    public PaymentOrder(Long id, Double amount, String currency, PaymentStatus status) {
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

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}