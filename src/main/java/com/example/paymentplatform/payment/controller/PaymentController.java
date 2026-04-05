package com.example.paymentplatform.payment.controller;

import com.example.paymentplatform.payment.domain.PaymentStatus;
import com.example.paymentplatform.payment.dto.ApprovePaymentRequest;
import com.example.paymentplatform.payment.dto.CancelPaymentRequest;
import com.example.paymentplatform.payment.dto.PaymentResponse;
import com.example.paymentplatform.payment.entity.Payment;
import com.example.paymentplatform.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/merchants")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{merchantId}/payments/approvals")
    public PaymentResponse approvePayment(@PathVariable String merchantId,
                                          @Valid @RequestBody ApprovePaymentRequest request) {
        Payment payment = paymentService.approvePayment(
                merchantId,
                request.orderId(),
                request.paymentId(),
                request.amount()
        );
        return PaymentResponse.from(payment);
    }

    @PostMapping("/{merchantId}/payments/{paymentId}/cancels")
    public PaymentResponse cancelPayment(@PathVariable String merchantId,
                                         @PathVariable String paymentId,
                                         @Valid @RequestBody CancelPaymentRequest request) {

        Payment payment = paymentService.cancelPayment(
                merchantId,
                paymentId,
                request.cancelAmount()
        );
        return PaymentResponse.from(payment);
    }

    @GetMapping("/{merchantId}/payments/{paymentId}")
    public PaymentResponse getPaymentByPaymentId(@PathVariable String merchantId,
                                                 @PathVariable String paymentId) {
        return PaymentResponse.from(paymentService.getPaymentByPaymentId(merchantId, paymentId));
    }

    @GetMapping("/{merchantId}/payments/orders/{orderId}")
    public PaymentResponse getPaymentByOrderId(@PathVariable String merchantId,
                                               @PathVariable String orderId) {
        return PaymentResponse.from(paymentService.getPaymentByOrderId(merchantId, orderId));
    }

    @GetMapping("/{merchantId}/payments")
    public Page<PaymentResponse> getPayments(@PathVariable String merchantId,
                                             @RequestParam(required = false) PaymentStatus status,
                                             @RequestParam(required = false, defaultValue = "0") int page,
                                             @RequestParam(required = false, defaultValue = "20") int size) {

        Page<Payment> payments = paymentService.getPayments(merchantId, status, PageRequest.of(page, size, Sort.by("createdAt").descending()));
        return payments.map(PaymentResponse::from);
    }
}
