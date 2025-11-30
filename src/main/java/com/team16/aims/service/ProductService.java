package com.hust.aims.service;

import org.springframework.stereotype.Service;

@Service
public class ProductService {

    public boolean validateProductPrice(double originalPrice, double currentPrice) {
        return false;
    }

    // public boolean validateProductPrice(double originalPrice, double currentPrice) {
    //     if (originalPrice <= 0) return false;
    //     return currentPrice >= (originalPrice * 0.3) && currentPrice <= (originalPrice * 1.5);
    // }
}