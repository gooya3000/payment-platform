package com.example.paymentplatform.payment.dto;

import com.example.paymentplatform.payment.domain.PaymentStatus;

public record PaymentResponse(
        String paymentId,
        String merchantId,
        String orderId,
        Long approvedAmount,
        Long canceledAmount,
        PaymentStatus status
) {
}
