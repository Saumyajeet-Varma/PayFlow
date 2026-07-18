package com.payflow.payflow.controller;

import com.payflow.payflow.dto.request.CreateOrderRequest;
import com.payflow.payflow.dto.request.ProcessPaymentRequest;
import com.payflow.payflow.dto.response.ApiResponse;
import com.payflow.payflow.dto.response.PaymentOrderResponse;
import com.payflow.payflow.dto.response.RefundResponse;
import com.payflow.payflow.service.PaymentOrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentOrderController {

    private final PaymentOrderService paymentOrderService;

    public PaymentOrderController(PaymentOrderService paymentOrderService) {
        this.paymentOrderService = paymentOrderService;
    }

    @PostMapping("/create-order")
    public ApiResponse<PaymentOrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return paymentOrderService.createOrder(request);
    }

    @PostMapping("/process/{orderId}")
    public ApiResponse<PaymentOrderResponse> processPayment(@PathVariable Long orderId, @RequestHeader("Idempotency-Key") String idempotencyKey, @Valid @RequestBody ProcessPaymentRequest request) {
        return paymentOrderService.processPayment(orderId, idempotencyKey, request);
    }

    @PostMapping("/refund/{orderId}")
    public ApiResponse<RefundResponse> refundPayment(@PathVariable Long orderId) {
        return paymentOrderService.refundPayment(orderId);
    }
}