package com.payflow.payflow.service;

import com.payflow.payflow.dto.request.CreateOrderRequest;
import com.payflow.payflow.dto.request.ProcessPaymentRequest;
import com.payflow.payflow.dto.response.ApiResponse;
import com.payflow.payflow.dto.response.PaymentOrderResponse;
import com.payflow.payflow.dto.response.RefundResponse;
import com.payflow.payflow.exception.ResourceNotFoundException;
import com.payflow.payflow.model.entity.Merchant;
import com.payflow.payflow.model.entity.PaymentOrder;
import com.payflow.payflow.model.entity.PaymentTransaction;
import com.payflow.payflow.model.enums.PaymentStatus;
import com.payflow.payflow.model.enums.TransactionStatus;
import com.payflow.payflow.repository.MerchantRepository;
import com.payflow.payflow.repository.PaymentOrderRepository;
import com.payflow.payflow.repository.PaymentTransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class PaymentOrderService {

    private final PaymentOrderRepository paymentOrderRepository;
    private final MerchantRepository merchantRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final MerchantService merchantService;

    public PaymentOrderService(PaymentOrderRepository paymentOrderRepository, MerchantRepository merchantRepository, PaymentTransactionRepository paymentTransactionRepository, MerchantService merchantService) {
        this.paymentOrderRepository = paymentOrderRepository;
        this.merchantRepository = merchantRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.merchantService = merchantService;
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

        Merchant loggedInMerchant = merchantService.getLoggedInMerchant();

        if (!order.getMerchant().getId().equals(loggedInMerchant.getId())) {

            return new ApiResponse<>(
                    false,
                    "Access Denied",
                    null
            );
        }

        if(order.getStatus() != PaymentStatus.CREATED) {

            return new ApiResponse<>(
                    false,
                    "Payment already processed",
                    null
            );
        }

        order.setStatus(PaymentStatus.PROCESSING);

        paymentOrderRepository.save(order);

        PaymentTransaction transaction = new PaymentTransaction();

        transaction.setAmount(order.getAmount());
        transaction.setPaymentMethod(request.getPaymentMethod());
        transaction.setPaymentOrder(order);
        transaction.setStatus(TransactionStatus.INITIATED);

        paymentTransactionRepository.save(transaction);

        // Simulate payment success/failure
        boolean paymentSuccess = Math.random() > 0.2;

        if(paymentSuccess) {
            order.setStatus(PaymentStatus.SUCCESS);
            transaction.setStatus(TransactionStatus.SUCCESS);
        }
        else {
            order.setStatus(PaymentStatus.FAILED);
            transaction.setStatus(TransactionStatus.FAILED);
        }

        paymentOrderRepository.save(order);
        paymentTransactionRepository.save(transaction);

        PaymentOrderResponse response = new PaymentOrderResponse(
                order.getId(),
                order.getAmount(),
                order.getCurrency(),
                order.getStatus()
        );

        return new ApiResponse<>(
                true,
                "Payment processed",
                response
        );
    }

    public ApiResponse<RefundResponse> refundPayment(Long orderId) {

        PaymentOrder order = paymentOrderRepository
                .findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        Merchant loggedInMerchant = merchantService.getLoggedInMerchant();

        if(!order.getMerchant().getId().equals(loggedInMerchant.getId())) {

            return new ApiResponse<>(
                    false,
                    "Access Denied",
                    null
            );
        }

        if(order.getStatus() != PaymentStatus.SUCCESS) {

            return new ApiResponse<>(
                    false,
                    "Only successful payments can be refunded",
                    null
            );
        }

        PaymentTransaction transaction = new PaymentTransaction();

        transaction.setAmount(order.getAmount());
        transaction.setPaymentMethod("REFUND");
        transaction.setStatus(TransactionStatus.REFUNDED);
        transaction.setPaymentOrder(order);

        paymentTransactionRepository.save(transaction);

        order.setStatus(PaymentStatus.REFUNDED);

        paymentOrderRepository.save(order);

        RefundResponse response = new RefundResponse(
                order.getId(),
                order.getStatus()
        );

        return new ApiResponse<>(
                true,
                "Refund Processing Successfully",
                response
        );
    }
}
