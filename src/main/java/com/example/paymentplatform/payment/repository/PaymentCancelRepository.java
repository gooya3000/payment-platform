package com.example.paymentplatform.payment.repository;

import com.example.paymentplatform.payment.entity.PaymentCancel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentCancelRepository extends JpaRepository<PaymentCancel, Long> {
}
