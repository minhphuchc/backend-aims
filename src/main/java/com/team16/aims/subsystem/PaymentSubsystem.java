package com.team16.aims.subsystem;

import com.team16.aims.entity.Order;

public interface PaymentSubsystem {
    String generatePaymentUrl(Order order);
}
