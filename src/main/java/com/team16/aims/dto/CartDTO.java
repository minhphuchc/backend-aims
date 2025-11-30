package com.team16.aims.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Integer cartId;
    private Integer userId;
    private List<CartItemDTO> items;
    private Double totalPrice;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemDTO {
        private Integer cartMediaId;
        private Integer mediaId;
        private String mediaTitle;
        private String mediaType;
        private Integer quantity;
        private Double price;
        private String imageUrl; // Optional, if we had images
    }
}
