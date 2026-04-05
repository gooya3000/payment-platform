package com.example.paymentplatform.common.exception;

public class PaymentApprovalInProgressException extends BusinessException {

    public PaymentApprovalInProgressException() {
        super(ErrorCode.PAYMENT_APPROVAL_IN_PROGRESS);
    }
}
