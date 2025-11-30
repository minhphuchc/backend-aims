package com.team16.aims.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "`Order`")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderId;

    private String customerName;
    private String customerPhone;
    private String customerEmail;

    private String shippingAddress;
    private String shippingProvince;
    private String shippingInstructions;

    private String orderStatus;

    private LocalDateTime createdAt;

    private Double subtotal;
    private Double shippingFee;
    private Double vat;
    private Double totalAmount;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> items;
}
