package com.payflow.payflow.controller;

import com.payflow.payflow.model.enums.PaymentStatus;
import com.payflow.payflow.model.entity.PaymentOrder;
import com.payflow.payflow.repository.PaymentOrderRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
public class PaymentOrderController {

    private final PaymentOrderRepository paymentOrderRepository;

    public PaymentOrderController(PaymentOrderRepository paymentOrderRepository) {
        this.paymentOrderRepository = paymentOrderRepository;
    }

    @PostMapping("/create-order")
    public PaymentOrder createOrder(@RequestBody PaymentOrder paymentOrder) {
        paymentOrder.setStatus(PaymentStatus.CREATED);
        return paymentOrderRepository.save(paymentOrder);
    }
}