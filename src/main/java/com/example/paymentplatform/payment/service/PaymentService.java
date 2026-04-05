package com.example.paymentplatform.payment.service;

import com.example.paymentplatform.common.exception.IdempotencyConflictException;
import com.example.paymentplatform.common.exception.DuplicatePaymentRequestException;
import com.example.paymentplatform.common.exception.PaymentApprovalInProgressException;
import com.example.paymentplatform.common.exception.PaymentNotFoundException;
import com.example.paymentplatform.idempotency.domain.ResourceType;
import com.example.paymentplatform.idempotency.entity.IdempotencyKey;
import com.example.paymentplatform.idempotency.repository.IdempotencyKeyRepository;
import com.example.paymentplatform.idempotency.service.IdempotencyHashGenerator;
import com.example.paymentplatform.payment.domain.PaymentStatus;
import com.example.paymentplatform.payment.entity.Payment;
import com.example.paymentplatform.payment.entity.PaymentCancel;
import com.example.paymentplatform.payment.entity.PaymentEvent;
import com.example.paymentplatform.payment.repository.PaymentCancelRepository;
import com.example.paymentplatform.payment.repository.PaymentEventRepository;
import com.example.paymentplatform.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final IdempotencyHashGenerator idempotencyHashGenerator;
    private final PaymentRepository paymentRepository;
    private final PaymentEventRepository paymentEventRepository;
    private final PaymentCancelRepository paymentCancelRepository;

    @Transactional
    public Payment approvePayment(String merchantId, String orderId, String paymentId, Long amount) {
        String requestHash = idempotencyHashGenerator.generate(orderId, amount);

        return idempotencyKeyRepository.findByMerchantIdAndIdempotencyKey(merchantId, paymentId)
                .map(existingKey -> handleExistingIdempotencyKey(existingKey, requestHash))
                .orElseGet(() -> createPaymentWithIdempotencyKey(merchantId, orderId, paymentId, amount, requestHash));
    }

    private Payment handleExistingIdempotencyKey(IdempotencyKey existingKey, String requestHash) {
        if (!existingKey.hasSameRequestHash(requestHash)) {
            throw new IdempotencyConflictException();
        }

        if (!existingKey.isCompleted()) {
            throw new PaymentApprovalInProgressException();
        }

        return paymentRepository.findById(Long.valueOf(existingKey.getResourceId()))
                .orElseThrow(PaymentNotFoundException::new);
    }

    private Payment createPaymentWithIdempotencyKey(String merchantId, String orderId, String paymentId, Long amount,
                                                    String requestHash) {
        IdempotencyKey idempotencyKey = idempotencyKeyRepository.save(
                IdempotencyKey.inProgress(merchantId, paymentId, requestHash)
        );

        try {

            Payment savedPayment = paymentRepository.saveAndFlush(Payment.approve(merchantId, paymentId, orderId, amount));
            paymentEventRepository.save(PaymentEvent.approved(savedPayment.getPaymentNo(), savedPayment.getApprovedAmount()));
            idempotencyKey.complete(ResourceType.PAYMENT, savedPayment.getPaymentNo().toString());

            return savedPayment;

        } catch (DataIntegrityViolationException e) {
            throw new DuplicatePaymentRequestException();
        }

    }

    @Transactional
    public Payment cancelPayment(String merchantId, String paymentId, Long cancelAmount) {
        return paymentRepository.findByMerchantIdAndPaymentIdForUpdate(merchantId, paymentId)
                .map( existingPayment -> updatePaymentWithCancel(existingPayment, cancelAmount))
                .orElseThrow(PaymentNotFoundException::new);
    }

    private Payment updatePaymentWithCancel(Payment existingPayment, Long cancelAmount) {
        existingPayment.cancel(cancelAmount);
        paymentCancelRepository.save(PaymentCancel.cancel(existingPayment.getPaymentNo(), cancelAmount));
        paymentEventRepository.save(PaymentEvent.canceled(existingPayment.getPaymentNo(), cancelAmount));
        return existingPayment;
    }

    public Payment getPaymentByPaymentId(String merchantId, String paymentId) {
        return paymentRepository.findByMerchantIdAndPaymentId(merchantId, paymentId)
                .orElseThrow(PaymentNotFoundException::new);
    }

    public Payment getPaymentByOrderId(String merchantId, String orderId) {
        return paymentRepository.findByMerchantIdAndOrderId(merchantId, orderId)
                .orElseThrow(PaymentNotFoundException::new);
    }

    public Page<Payment> getPayments(String merchantId, PaymentStatus status, Pageable pageable) {
        if (status == null) {
            return paymentRepository.findByMerchantId(merchantId, pageable);
        }
        return paymentRepository.findByMerchantIdAndStatus(merchantId, status, pageable);
    }
}
