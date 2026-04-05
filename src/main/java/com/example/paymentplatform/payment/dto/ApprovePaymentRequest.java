package com.example.paymentplatform.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ApprovePaymentRequest(
        @NotBlank String orderId,
        @NotBlank String paymentId,
        @NotNull @Positive Long amount
) {
}
