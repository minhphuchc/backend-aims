package com.team16.aims.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Media")
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer mediaId;

    private String title;

    private String category;

    private Double value;

    private Double price;

    private Integer quantity;

    private String imageUrl;

    private String description;

    private Float weight;

    private String dimensions;

    private String barcode;

    private String mediaType; // Book / CD / DVD / Newspaper
}
