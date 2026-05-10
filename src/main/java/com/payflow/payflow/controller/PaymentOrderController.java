package com.payflow.payflow.controller;

import com.payflow.payflow.dto.request.CreateOrderRequest;
import com.payflow.payflow.dto.response.ApiResponse;
import com.payflow.payflow.dto.response.PaymentOrderResponse;
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
}