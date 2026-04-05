package com.example.paymentplatform.idempotency.entity;

import com.example.paymentplatform.idempotency.domain.IdempotencyStatus;
import com.example.paymentplatform.idempotency.domain.ResourceType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "idempotency_key",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_idempotency_key_merchant_idempotency_key", columnNames = {"merchant_id", "idempotency_key"})
        },
        indexes = {
                @Index(name = "idx_idempotency_key_expired_at", columnList = "expired_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IdempotencyKey {
    @Id
    @GeneratedValue
    @Column(name = "id")
    private Long id;
    @Column(name = "merchant_id", nullable = false)
    private String merchantId;
    @Column(name = "idempotency_key", nullable = false)
    private String idempotencyKey;
    @Column(name = "request_hash", nullable = false)
    private String requestHash;
    @Column(name = "resource_type")
    @Enumerated(EnumType.STRING)
    private ResourceType resourceType;
    @Column(name = "resource_id")
    private String resourceId;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private IdempotencyStatus status;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    private IdempotencyKey(String merchantId, String idempotencyKey, String requestHash) {
        this.merchantId = merchantId;
        this.idempotencyKey = idempotencyKey;
        this.requestHash = requestHash;
        this.status = IdempotencyStatus.IN_PROGRESS;
    }

    public static IdempotencyKey inProgress(String merchantId, String idempotencyKey, String requestHash) {
        return new IdempotencyKey(merchantId, idempotencyKey, requestHash);
    }

    public void complete(ResourceType resourceType, String resourceId) {
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.status = IdempotencyStatus.COMPLETED;
    }

    public boolean hasSameRequestHash(String requestHash) {
        return this.requestHash.equals(requestHash);
    }

    public boolean isCompleted() {
        return this.status == IdempotencyStatus.COMPLETED;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.expiredAt = now.plusHours(24);
    }
}
