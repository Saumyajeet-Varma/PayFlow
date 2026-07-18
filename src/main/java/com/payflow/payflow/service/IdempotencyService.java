package com.payflow.payflow.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payflow.payflow.dto.response.ApiResponse;
import com.payflow.payflow.dto.response.PaymentOrderResponse;
import com.payflow.payflow.exception.IdempotencyConflictException;
import com.payflow.payflow.model.entity.IdempotencyRecord;
import com.payflow.payflow.model.entity.Merchant;
import com.payflow.payflow.repository.IdempotencyRecordRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class IdempotencyService {

    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final ObjectMapper objectMapper;

    public IdempotencyService(IdempotencyRecordRepository idempotencyRecordRepository, ObjectMapper objectMapper) {
        this.idempotencyRecordRepository = idempotencyRecordRepository;
        this.objectMapper = objectMapper;
    }

    public Optional<ApiResponse<PaymentOrderResponse>> findCached(
            Long merchantId,
            String idempotencyKey,
            String requestPath) {

        return idempotencyRecordRepository
                .findByMerchantIdAndIdempotencyKey(merchantId, idempotencyKey)
                .map(record -> {
                    if (!record.getRequestPath().equals(requestPath)) {
                        throw new IdempotencyConflictException(
                                "Idempotency key was already used for a different request"
                        );
                    }
                    return deserialize(record.getResponseBody());
                });
    }

    public void save(
            Merchant merchant,
            String idempotencyKey,
            String requestPath,
            ApiResponse<PaymentOrderResponse> response) {

        IdempotencyRecord record = new IdempotencyRecord();

        record.setMerchant(merchant);
        record.setIdempotencyKey(idempotencyKey);
        record.setRequestPath(requestPath);
        record.setResponseBody(serialize(response));

        try {
            idempotencyRecordRepository.save(record);
        } catch (DataIntegrityViolationException ex) {
            findCached(merchant.getId(), idempotencyKey, requestPath)
                    .orElseThrow(() -> ex);
        }
    }

    private String serialize(ApiResponse<PaymentOrderResponse> response) {
        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize idempotency response", ex);
        }
    }

    private ApiResponse<PaymentOrderResponse> deserialize(String responseBody) {
        try {
            return objectMapper.readValue(
                    responseBody,
                    new TypeReference<ApiResponse<PaymentOrderResponse>>() {}
            );
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to deserialize idempotency response", ex);
        }
    }
}
