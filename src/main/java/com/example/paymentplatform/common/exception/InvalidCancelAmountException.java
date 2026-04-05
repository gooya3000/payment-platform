package com.example.paymentplatform.common.exception;

public class InvalidCancelAmountException extends BusinessException {

    public InvalidCancelAmountException() {
        super(ErrorCode.INVALID_CANCEL_AMOUNT);
    }
}
