package com.team16.aims.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentTransactionDTO {
    private String transactionId;
    private Integer orderId;
    private String transactionContent;
    private Double amount;
    private String paymentMethod;
    private String status;
    private LocalDateTime createdAt;
}
