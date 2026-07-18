package com.payflow.payflow.repository;

import com.payflow.payflow.model.entity.IdempotencyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IdempotencyRecordRepository extends JpaRepository<IdempotencyRecord, Long> {

    Optional<IdempotencyRecord> findByMerchantIdAndIdempotencyKey(Long merchantId, String idempotencyKey);
}
