package com.example.paymentplatform.payment.dto;

import com.example.paymentplatform.payment.domain.PaymentStatus;
import com.example.paymentplatform.payment.entity.Payment;

public record PaymentResponse(
        String paymentId,
        String merchantId,
        String orderId,
        Long approvedAmount,
        Long canceledAmount,
        PaymentStatus status
) {
    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getPaymentId(),
                payment.getMerchantId(),
                payment.getOrderId(),
                payment.getApprovedAmount(),
                payment.getCanceledAmount(),
                payment.getStatus()
        );
    }
}
