package com.payflow.payflow.service;

import com.payflow.payflow.dto.request.CreateOrderRequest;
import com.payflow.payflow.dto.request.ProcessPaymentRequest;
import com.payflow.payflow.dto.response.ApiResponse;
import com.payflow.payflow.dto.response.PaymentOrderResponse;
import com.payflow.payflow.exception.ResourceNotFoundException;
import com.payflow.payflow.model.entity.Merchant;
import com.payflow.payflow.model.entity.PaymentOrder;
import com.payflow.payflow.model.enums.PaymentStatus;
import com.payflow.payflow.repository.MerchantRepository;
import com.payflow.payflow.repository.PaymentOrderRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentOrderService {

    private final PaymentOrderRepository paymentOrderRepository;
    private final MerchantRepository merchantRepository;

    public PaymentOrderService(PaymentOrderRepository paymentOrderRepository, MerchantRepository merchantRepository) {
        this.paymentOrderRepository = paymentOrderRepository;
        this.merchantRepository = merchantRepository;
    }

    public ApiResponse<PaymentOrderResponse> createOrder(CreateOrderRequest request) {

        Merchant merchant = merchantRepository
                .findById(request.getMerchantId())
                .orElseThrow(() -> new ResourceNotFoundException("Merchant Not Found"));

        PaymentOrder order = new PaymentOrder();

        order.setAmount(request.getAmount());
        order.setCurrency(request.getCurrency());
        order.setStatus(PaymentStatus.CREATED);
        order.setMerchant(merchant);

        PaymentOrder savedOrder = paymentOrderRepository.save(order);

        PaymentOrderResponse response = new PaymentOrderResponse(
                savedOrder.getId(),
                savedOrder.getAmount(),
                savedOrder.getCurrency(),
                savedOrder.getStatus()
        );

        return new ApiResponse<>(
                true,
                "Order Created Successfully",
                response
        );
    }

    public ApiResponse<PaymentOrderResponse> processPayment(long orderId, ProcessPaymentRequest request) {

        PaymentOrder order = paymentOrderRepository
                .findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if(order.getStatus() != PaymentStatus.CREATED) {

            return new ApiResponse<>(
                    false,
                    "Payment already processed",
                    null
            );
        }

        order.setStatus(PaymentStatus.PROCESSING);

        /*
            Simulate payment success/failure
        */

        boolean paymentSuccess = Math.random() > 0.2;

        if(paymentSuccess) {
            order.setStatus(PaymentStatus.SUCCESS);
        }
        else {
            order.setStatus(PaymentStatus.FAILED);
        }

        PaymentOrder savedOrder = paymentOrderRepository.save(order);

        PaymentOrderResponse response = new PaymentOrderResponse(
                savedOrder.getId(),
                savedOrder.getAmount(),
                savedOrder.getCurrency(),
                savedOrder.getStatus()
        );

        return new ApiResponse<>(
                true,
                "Payment processed",
                response
        );
    }
}
