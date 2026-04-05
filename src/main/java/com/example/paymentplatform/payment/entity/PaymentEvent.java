package com.example.paymentplatform.payment.entity;

import com.example.paymentplatform.payment.domain.PaymentEventType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "payment_event",
        indexes = {
                @Index(name = "idx_payment_event_payment_no_created_at", columnList = "payment_no, created_at")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentEvent {
    @Id
    @GeneratedValue
    @Column(name = "event_id")
    private Long eventId;
    @Column(name = "payment_no", nullable = false)
    private Long paymentNo;
    @Column(name = "event_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentEventType eventType;
    @Column(name = "event_amount", nullable = false)
    private Long eventAmount;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private PaymentEvent(Long paymentNo, PaymentEventType eventType, Long eventAmount) {
        this.paymentNo = paymentNo;
        this.eventType = eventType;
        this.eventAmount = eventAmount;
    }

    public static PaymentEvent approved(Long paymentNo, Long approvedAmount) {
        return new PaymentEvent(paymentNo, PaymentEventType.APPROVED, approvedAmount);
    }

    public static PaymentEvent canceled(Long paymentNo, Long cancelledAmount) {
        return new PaymentEvent(paymentNo, PaymentEventType.CANCELED, cancelledAmount);
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

}
