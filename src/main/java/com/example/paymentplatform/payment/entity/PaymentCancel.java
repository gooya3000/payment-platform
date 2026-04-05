package com.example.paymentplatform.payment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "payment_cancel",
        indexes = {
                @Index(name = "idx_payment_cancel_payment_no", columnList = "payment_no"),
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentCancel {
    @Id
    @GeneratedValue
    @Column(name = "cancel_id")
    private Long cancelId;
    @Column(name = "payment_no", nullable = false)
    private Long paymentNo;
    @Column(name = "cancel_amount", nullable = false)
    private Long cancelAmount;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public static PaymentCancel cancel(Long paymentNo, Long cancelAmount) {
        return new PaymentCancel(paymentNo, cancelAmount);
    }

    private PaymentCancel(Long paymentNo, Long cancelAmount) {
        this.paymentNo = paymentNo;
        this.cancelAmount = cancelAmount;
    }


    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

}
