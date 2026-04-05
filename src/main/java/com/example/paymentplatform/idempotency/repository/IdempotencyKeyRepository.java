package com.example.paymentplatform.idempotency.repository;

import com.example.paymentplatform.idempotency.entity.IdempotencyKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdempotencyKeyRepository extends JpaRepository<IdempotencyKey, Long> {
    Optional<IdempotencyKey> findByMerchantIdAndIdempotencyKey(String merchantId, String idempotencyKey);
}
