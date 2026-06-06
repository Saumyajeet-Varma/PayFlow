package com.payflow.payflow.dto.response;

import com.payflow.payflow.model.enums.PaymentStatus;

public class RefundResponse {

    private Long orderId;
    private PaymentStatus status;

    public RefundResponse(Long orderId, PaymentStatus status) {
        this.orderId = orderId;
        this.status = status;
    }

    public Long getOrderId() { return orderId; }

    public PaymentStatus getStatus() { return status; }
}
