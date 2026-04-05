package com.example.paymentplatform.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    IDEMPOTENCY_CONFLICT(HttpStatus.CONFLICT, "IDEMPOTENCY_CONFLICT", "동일한 paymentId에 대해 다른 요청 정보가 전달되었습니다."),
    PAYMENT_APPROVAL_IN_PROGRESS(HttpStatus.CONFLICT, "PAYMENT_APPROVAL_IN_PROGRESS", "결제 승인이 이미 처리 중입니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT_NOT_FOUND", "결제를 찾을 수 없습니다."),
    DUPLICATE_PAYMENT_REQUEST(HttpStatus.CONFLICT, "DUPLICATE_PAYMENT_REQUEST", "이미 처리되었거나 중복된 결제 요청입니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", "잘못된 요청입니다."),
    CANCEL_AMOUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "CANCEL_AMOUNT_EXCEEDED", "취소 가능 금액을 초과하였습니다."),
    INVALID_CANCEL_AMOUNT(HttpStatus.BAD_REQUEST, "INVALID_CANCEL_AMOUNT", "취소 금액은 1원 이상이어야 합니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
