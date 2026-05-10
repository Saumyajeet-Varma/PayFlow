package com.payflow.payflow.service;

import com.payflow.payflow.core.dto.request.CreateOrderRequest;
import com.payflow.payflow.core.dto.response.ApiResponse;
import com.payflow.payflow.core.dto.response.PaymentOrderResponse;
import com.payflow.payflow.core.entity.Merchant;
import com.payflow.payflow.core.entity.PaymentOrder;
import com.payflow.payflow.core.enums.PaymentStatus;
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

    public ApiResponse<PaymentOrderResponse>
    createOrder(CreateOrderRequest request) {

        Merchant merchant = merchantRepository
                .findById(request.getMerchantId())
                .orElseThrow(() -> new RuntimeException("Merchant Not Found"));

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
}
