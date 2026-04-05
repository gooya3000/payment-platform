package com.example.paymentplatform.common.response;

public record ErrorResponse(
        String code,
        String message
) {
}
