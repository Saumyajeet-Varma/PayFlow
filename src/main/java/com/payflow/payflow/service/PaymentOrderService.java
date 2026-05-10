package com.payflow.payflow.service;

import com.payflow.payflow.core.dto.CreateOrderRequest;
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

    public PaymentOrder createOrder(CreateOrderRequest request) {

        Merchant merchant = merchantRepository.findById(request.getMerchantId())
                .orElseThrow(() -> new RuntimeException("Merchant not found"));

        PaymentOrder order = new PaymentOrder();

        order.setAmount(request.getAmount());
        order.setCurrency(request.getCurrency());
        order.setStatus(PaymentStatus.CREATED);
        order.setMerchant(merchant);

        return paymentOrderRepository.save(order);
    }
}
