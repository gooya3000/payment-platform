package com.example.paymentplatform.payment.service;

import com.example.paymentplatform.common.exception.CancelAmountExceededException;
import com.example.paymentplatform.common.exception.DuplicatePaymentRequestException;
import com.example.paymentplatform.common.exception.IdempotencyConflictException;
import com.example.paymentplatform.payment.domain.PaymentStatus;
import com.example.paymentplatform.payment.entity.Payment;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PaymentServiceTest {

    private static final String MERCHANT_ID = "merchant-1";
    private static final String ORDER_ID = "order-1";
    private static final String PAYMENT_ID = "payment-1";
    private static final Long APPROVED_AMOUNT = 10_000L;
    private static final Long CANCEL_AMOUNT = 3_000L;

    @Autowired
    private PaymentService paymentService;

    @Test
    void approve_payment_success() {
        Payment payment = paymentService.approvePayment(
                merchantId(),
                orderId(),
                paymentId(),
                approvedAmount()
        );

        assertThat(payment.getMerchantId()).isEqualTo(merchantId());
        assertThat(payment.getOrderId()).isEqualTo(orderId());
        assertThat(payment.getPaymentId()).isEqualTo(paymentId());
        assertThat(payment.getApprovedAmount()).isEqualTo(approvedAmount());
        assertThat(payment.getCanceledAmount()).isZero();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.APPROVED);
    }

    @Test
    void approve_payment_throws_exception_when_different_request_uses_same_payment_id() {
        paymentService.approvePayment(
                merchantId(),
                orderId(),
                paymentId(),
                approvedAmount()
        );

        assertThatThrownBy(() -> paymentService.approvePayment(
                merchantId(),
                "different-order",
                paymentId(),
                approvedAmount()
        )).isInstanceOf(IdempotencyConflictException.class);
    }

    @Test
    void approve_payment_throws_exception_when_different_request_uses_same_order_id() {
        paymentService.approvePayment(
                merchantId(),
                orderId(),
                paymentId(),
                approvedAmount()
        );

        assertThatThrownBy(() -> paymentService.approvePayment(
                merchantId(),
                orderId(),
                "different-payment-id",
                approvedAmount()
        )).isInstanceOf(DuplicatePaymentRequestException.class);
    }

    @Test
    void cancel_payment_success() {
        paymentService.approvePayment(
                merchantId(),
                orderId(),
                paymentId(),
                approvedAmount()
        );

        Payment canceledPayment = paymentService.cancelPayment(
                merchantId(),
                paymentId(),
                cancelAmount()
        );

        assertThat(canceledPayment.getApprovedAmount()).isEqualTo(approvedAmount());
        assertThat(canceledPayment.getCanceledAmount()).isEqualTo(cancelAmount());
        assertThat(canceledPayment.getStatus()).isEqualTo(PaymentStatus.PARTIAL_CANCELED);
    }

    @Test
    void cancel_payment_throws_exception_when_cancel_amount_exceeds_approved_amount() {
        paymentService.approvePayment(
                merchantId(),
                orderId(),
                paymentId(),
                approvedAmount()
        );

        assertThatThrownBy(() -> paymentService.cancelPayment(
                merchantId(),
                paymentId(),
                approvedAmount() + 1L
        )).isInstanceOf(CancelAmountExceededException.class);
    }

    protected String merchantId() {
        return MERCHANT_ID;
    }

    protected String orderId() {
        return ORDER_ID;
    }

    protected String paymentId() {
        return PAYMENT_ID;
    }

    protected Long approvedAmount() {
        return APPROVED_AMOUNT;
    }

    protected Long cancelAmount() {
        return CANCEL_AMOUNT;
    }
}
