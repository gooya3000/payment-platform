package com.example.paymentplatform.common.exception;

public class IdempotencyConflictException extends BusinessException {

    public IdempotencyConflictException() {
        super(ErrorCode.IDEMPOTENCY_CONFLICT);
    }
}
