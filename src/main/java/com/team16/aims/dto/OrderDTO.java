package com.team16.aims.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
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

    // For creating order, we might need a list of items (mediaId, quantity)
    // But for viewing, we might want full details.
    // Let's create a nested ItemDTO or just use a simple structure for now.
    private List<OrderItemDTO> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemDTO {
        private Integer mediaId;
        private Integer quantity;
        private Double price;
    }
}
