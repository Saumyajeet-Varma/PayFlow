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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Service
public class PaymentOrderService {

    private final PaymentOrderRepository paymentOrderRepository;
    private final MerchantRepository merchantRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final MerchantService merchantService;
    private final IdempotencyService idempotencyService;

    public PaymentOrderService(
            PaymentOrderRepository paymentOrderRepository,
            MerchantRepository merchantRepository,
            PaymentTransactionRepository paymentTransactionRepository,
            MerchantService merchantService,
            IdempotencyService idempotencyService) {
        this.paymentOrderRepository = paymentOrderRepository;
        this.merchantRepository = merchantRepository;
        this.paymentTransactionRepository = paymentTransactionRepository;
        this.merchantService = merchantService;
        this.idempotencyService = idempotencyService;
    }

    public ApiResponse<PaymentOrderResponse> createOrder(CreateOrderRequest request) {

        Merchant merchant = merchantRepository
                .findById(request.getMerchantId())
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found"));

        PaymentOrder order = new PaymentOrder();

        order.setAmount(request.getAmount());
        order.setCurrency(request.getCurrency());
        order.setStatus(PaymentStatus.CREATED);
        order.setMerchant(merchant);

        PaymentOrder savedOrder = paymentOrderRepository.save(order);

        return ApiResponse.success(
                "Order created successfully",
                toPaymentOrderResponse(savedOrder)
        );
    }

    @Transactional
    public ApiResponse<PaymentOrderResponse> processPayment(
            long orderId,
            String idempotencyKey,
            ProcessPaymentRequest request) {

        if (!StringUtils.hasText(idempotencyKey)) {
            return ApiResponse.error("Idempotency-Key header is required");
        }

        Merchant loggedInMerchant = merchantService.getLoggedInMerchant();
        String requestPath = buildProcessPaymentPath(orderId);

        Optional<ApiResponse<PaymentOrderResponse>> cachedResponse = idempotencyService.findCached(
                loggedInMerchant.getId(),
                idempotencyKey,
                requestPath
        );

        if (cachedResponse.isPresent()) {
            return cachedResponse.get();
        }

        PaymentOrder order = paymentOrderRepository
                .findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getMerchant().getId().equals(loggedInMerchant.getId())) {
            return cacheAndReturn(
                    loggedInMerchant,
                    idempotencyKey,
                    requestPath,
                    ApiResponse.error("Access denied")
            );
        }

        if (order.getStatus() != PaymentStatus.CREATED) {
            return cacheAndReturn(
                    loggedInMerchant,
                    idempotencyKey,
                    requestPath,
                    ApiResponse.error("Payment already processed")
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

        boolean paymentSuccess = Math.random() > 0.2;

        if (paymentSuccess) {
            order.setStatus(PaymentStatus.SUCCESS);
            transaction.setStatus(TransactionStatus.SUCCESS);
        } else {
            order.setStatus(PaymentStatus.FAILED);
            transaction.setStatus(TransactionStatus.FAILED);
        }

        paymentOrderRepository.save(order);
        paymentTransactionRepository.save(transaction);

        return cacheAndReturn(
                loggedInMerchant,
                idempotencyKey,
                requestPath,
                ApiResponse.success(
                        "Payment processed successfully",
                        toPaymentOrderResponse(order)
                )
        );
    }

    public ApiResponse<RefundResponse> refundPayment(Long orderId) {

        PaymentOrder order = paymentOrderRepository
                .findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        Merchant loggedInMerchant = merchantService.getLoggedInMerchant();

        if (!order.getMerchant().getId().equals(loggedInMerchant.getId())) {
            return ApiResponse.error("Access denied");
        }

        if (order.getStatus() != PaymentStatus.SUCCESS) {
            return ApiResponse.error("Only successful payments can be refunded");
        }

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setAmount(order.getAmount());
        transaction.setPaymentMethod("REFUND");
        transaction.setStatus(TransactionStatus.REFUNDED);
        transaction.setPaymentOrder(order);
        paymentTransactionRepository.save(transaction);

        order.setStatus(PaymentStatus.REFUNDED);
        paymentOrderRepository.save(order);

        return ApiResponse.success(
                "Refund processed successfully",
                new RefundResponse(order.getId(), order.getStatus())
        );
    }

    private ApiResponse<PaymentOrderResponse> cacheAndReturn(
            Merchant merchant,
            String idempotencyKey,
            String requestPath,
            ApiResponse<PaymentOrderResponse> response) {

        idempotencyService.save(merchant, idempotencyKey, requestPath, response);
        return response;
    }

    private PaymentOrderResponse toPaymentOrderResponse(PaymentOrder order) {
        return new PaymentOrderResponse(
                order.getId(),
                order.getAmount(),
                order.getCurrency(),
                order.getStatus()
        );
    }

    private String buildProcessPaymentPath(long orderId) {
        return "POST /payment/process/" + orderId;
    }
}
