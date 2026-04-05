package com.example.paymentplatform.common.exception;

public class DuplicatePaymentRequestException extends BusinessException {

    public DuplicatePaymentRequestException() {
        super(ErrorCode.DUPLICATE_PAYMENT_REQUEST);
    }
}
