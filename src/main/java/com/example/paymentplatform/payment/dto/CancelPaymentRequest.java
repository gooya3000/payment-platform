package com.example.paymentplatform.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CancelPaymentRequest(
        @NotNull @Positive Long cancelAmount
) {
}
