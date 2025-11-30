package com.team16.aims.repository;

import com.team16.aims.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentTransactionRepo extends JpaRepository<PaymentTransaction, String> {
}
