package com.payflow.payflow.controller;

import com.payflow.payflow.core.dto.CreateOrderRequest;
import com.payflow.payflow.core.entity.PaymentOrder;
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
    public PaymentOrder createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        return paymentOrderService.createOrder(createOrderRequest);
    }
}