package com.team16.aims.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "PaymentTransaction")
public class PaymentTransaction {

    @Id
    private String transactionId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private String transactionContent;
    private Double amount;
    private String paymentMethod;
    private String status;

    private LocalDateTime createdAt;
}
