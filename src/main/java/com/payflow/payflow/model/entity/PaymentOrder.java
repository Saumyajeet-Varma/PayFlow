package com.payflow.payflow.model.entity;

import com.payflow.payflow.model.enums.PaymentStatus;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;

    private String currency;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @ManyToOne
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    @OneToMany(mappedBy = "paymentOrder")
    private List<PaymentTransaction> transactions;

    public PaymentOrder() {}

    public PaymentOrder(Long id, Double amount, String currency, PaymentStatus status, Merchant merchant) {
        this.id = id;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.merchant = merchant;
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

    public Merchant getMerchant() {
        return merchant;
    }

    public void setMerchant(Merchant merchant) {
        this.merchant = merchant;
    }
}