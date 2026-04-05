package com.example.paymentplatform.common.exception;

public class CancelAmountExceededException extends BusinessException {

    public CancelAmountExceededException() {
        super(ErrorCode.CANCEL_AMOUNT_EXCEEDED);
    }
}
