package com.example.paymentplatform.payment.repository;

import com.example.paymentplatform.payment.domain.PaymentStatus;
import com.example.paymentplatform.payment.entity.Payment;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Payment p where p.merchantId = :merchantId and p.paymentId = :paymentId")
    Optional<Payment> findByMerchantIdAndPaymentIdForUpdate(String merchantId, String paymentId);
    Optional<Payment> findByMerchantIdAndPaymentId(String merchantId, String paymentId);
    Optional<Payment> findByMerchantIdAndOrderId(String merchantId, String orderId);
    Page<Payment> findByMerchantId(String merchantId, Pageable pageable);
    Page<Payment> findByMerchantIdAndStatus(String merchantId, PaymentStatus status, Pageable pageable);
}
