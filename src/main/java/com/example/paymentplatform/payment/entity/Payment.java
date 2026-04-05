package com.example.paymentplatform.payment.entity;

import com.example.paymentplatform.common.exception.CancelAmountExceededException;
import com.example.paymentplatform.common.exception.InvalidCancelAmountException;
import com.example.paymentplatform.payment.domain.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "payment",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_payment_merchant_payment_id", columnNames = {"merchant_id", "payment_id"}),
                @UniqueConstraint(name = "uk_payment_merchant_order_id", columnNames = {"merchant_id", "order_id"})
        },
        indexes = {
                @Index(name = "idx_payment_merchant_payment_id", columnList = "merchant_id, payment_id"),
                @Index(name = "idx_payment_merchant_created_at", columnList = "merchant_id, created_at"),
                @Index(name = "idx_payment_merchant_status_created_at", columnList = "merchant_id, status, created_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {
    @Id
    @GeneratedValue
    @Column(name = "payment_no")
    private Long paymentNo;
    @Column(name = "payment_id", nullable = false)
    private String paymentId;
    @Column(name = "merchant_id", nullable = false)
    private String merchantId;
    @Column(name = "order_id", nullable = false)
    private String orderId;
    @Column(name = "approved_amount", nullable = false)
    private Long approvedAmount;
    @Column(name = "canceled_amount", nullable = false)
    private Long canceledAmount;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private Payment(String merchantId, String paymentId, String orderId, Long approvedAmount) {
        this.merchantId = merchantId;
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.approvedAmount = approvedAmount;
        this.canceledAmount = 0L;
        this.status = PaymentStatus.APPROVED;
    }

    public static Payment approve(String merchantId, String paymentId, String orderId, Long approvedAmount) {
        return new Payment(merchantId, paymentId, orderId, approvedAmount);
    }

    public void cancel(Long cancelAmount) {
        if (this.status.equals(PaymentStatus.CANCELED)) {
            throw new CancelAmountExceededException();
        }
        if (cancelAmount <= 0) {
            throw new InvalidCancelAmountException();
        }
        this.canceledAmount = this.canceledAmount + cancelAmount;
        if (this.approvedAmount < this.canceledAmount) {
            throw new CancelAmountExceededException();
        }
        this.status = this.approvedAmount.equals(this.canceledAmount) ? PaymentStatus.CANCELED : PaymentStatus.PARTIAL_CANCELED;
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
