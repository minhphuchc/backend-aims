package com.team16.aims.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Cart_Media")
public class CartMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer cartMediaId;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "media_id")
    private Media media;

    private Integer quantity;

    private Double price; // Price at the time of adding to cart, or current price
}
