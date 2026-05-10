package com.payflow.payflow.controller;

import com.payflow.payflow.core.dto.request.CreateOrderRequest;
import com.payflow.payflow.core.dto.response.ApiResponse;
import com.payflow.payflow.core.dto.response.PaymentOrderResponse;
import com.payflow.payflow.service.PaymentOrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentOrderController {

    private final PaymentOrderService paymentOrderService;

    public PaymentOrderController(PaymentOrderService paymentOrderService) {
        this.paymentOrderService = paymentOrderService;
    }

    @PostMapping("/create-order")
    public ApiResponse<PaymentOrderResponse> createOrder(@RequestBody CreateOrderRequest request) {
        return paymentOrderService.createOrder(request);
    }
}