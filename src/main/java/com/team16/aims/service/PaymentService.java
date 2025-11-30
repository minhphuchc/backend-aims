package com.team16.aims.service;

import com.team16.aims.dto.PaymentTransactionDTO;
import com.team16.aims.entity.Order;
import com.team16.aims.entity.PaymentTransaction;
import com.team16.aims.exception.ResourceNotFoundException;
import com.team16.aims.repository.OrderRepo;
import com.team16.aims.repository.PaymentTransactionRepo;
import com.team16.aims.subsystem.PaymentSubsystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentTransactionRepo paymentTransactionRepo;
    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private PaymentSubsystem paymentSubsystem;

    @Transactional
    public PaymentTransactionDTO processPayment(PaymentTransactionDTO transactionDTO) {
        Order order = orderRepo.findById(transactionDTO.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + transactionDTO.getOrderId()));

        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionId(UUID.randomUUID().toString()); // Generate ID if not provided
        transaction.setOrder(order);
        transaction.setTransactionContent(transactionDTO.getTransactionContent());
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setPaymentMethod(transactionDTO.getPaymentMethod());
        transaction.setStatus("SUCCESS"); // Assume success for now
        transaction.setCreatedAt(LocalDateTime.now());

        paymentTransactionRepo.save(transaction);

        // Update order status
        order.setOrderStatus("PAID");
        orderRepo.save(order);

        return convertToDTO(transaction);
    }

    public String getPaymentUrl(Integer orderId) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
        return paymentSubsystem.generatePaymentUrl(order);
    }

    private PaymentTransactionDTO convertToDTO(PaymentTransaction transaction) {
        return new PaymentTransactionDTO(
                transaction.getTransactionId(),
                transaction.getOrder().getOrderId(),
                transaction.getTransactionContent(),
                transaction.getAmount(),
                transaction.getPaymentMethod(),
                transaction.getStatus(),
                transaction.getCreatedAt());
    }
}
