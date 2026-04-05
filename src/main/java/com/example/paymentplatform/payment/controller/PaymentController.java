package com.example.paymentplatform.payment.controller;

import com.example.paymentplatform.payment.domain.PaymentStatus;
import com.example.paymentplatform.payment.dto.ApprovePaymentRequest;
import com.example.paymentplatform.payment.dto.CancelPaymentRequest;
import com.example.paymentplatform.payment.dto.PaymentResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/merchants")
public class PaymentController {

    @PostMapping("/{merchantId}/payments/approvals")
    public PaymentResponse approvePayment(@PathVariable String merchantId,
                                          @Valid @RequestBody ApprovePaymentRequest request) {
        return null;
    }

    @PostMapping("/{merchantId}/payments/{paymentId}/cancels")
    public PaymentResponse cancelPayment(@PathVariable String merchantId,
                                         @PathVariable String paymentId,
                                         @Valid @RequestBody CancelPaymentRequest request) {
        return null;
    }

    @GetMapping("/{merchantId}/payments/{paymentId}")
    public PaymentResponse getPaymentByPaymentId(@PathVariable String merchantId,
                                                 @PathVariable String paymentId) {
        return null;
    }

    @GetMapping("/{merchantId}/payments/orders/{orderId}")
    public PaymentResponse getPaymentByOrderId(@PathVariable String merchantId,
                                               @PathVariable String orderId) {
        return null;
    }

    @GetMapping("/{merchantId}/payments")
    public Page<PaymentResponse> getPayments(@PathVariable String merchantId,
                                             @RequestParam(required = false) PaymentStatus status,
                                             @RequestParam(required = false, defaultValue = "0") int page,
                                             @RequestParam(required = false, defaultValue = "20") int size) {
        return null;
    }


}
