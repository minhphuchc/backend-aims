package com.team16.aims.controller;

import com.team16.aims.dto.OrderDTO;
import com.team16.aims.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.team16.aims.repository.MediaRepo;

@RestController
@RequestMapping("/api/place-order")
@CrossOrigin(origins = "*")
public class PlaceOrderController {

    @Autowired
    private CartService cartService;

    @Autowired
    private MediaRepo mediaRepo;

    @Autowired
    private com.team16.aims.service.OrderService orderService;

    @PostMapping("/{userId}")
    public ResponseEntity<OrderDTO> placeOrder(@PathVariable Integer userId, @RequestBody OrderDTO orderInfo) {
        validateDeliveryInfo(orderInfo);
        // Recalculate shipping fee to ensure it's correct before placing order
        double shippingFee = calculateShippingFeeInternal(orderInfo);
        orderInfo.setShippingFee(shippingFee);
        return new ResponseEntity<>(cartService.placeOrder(userId, null, orderInfo), HttpStatus.CREATED);
    }

    @PostMapping("/cart")
    public ResponseEntity<OrderDTO> placeOrderFromCart(@RequestParam String sessionId,
            @RequestBody OrderDTO orderInfo) {
        validateDeliveryInfo(orderInfo);
        double shippingFee = calculateShippingFeeInternal(orderInfo);
        orderInfo.setShippingFee(shippingFee);
        return new ResponseEntity<>(cartService.placeOrder(null, sessionId, orderInfo), HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<OrderDTO> placeOrder(@RequestBody OrderDTO orderInfo) {
        validateDeliveryInfo(orderInfo);
        // Recalculate shipping fee
        double shippingFee = calculateShippingFeeInternal(orderInfo);
        orderInfo.setShippingFee(shippingFee);
        return new ResponseEntity<>(orderService.createOrder(orderInfo), HttpStatus.CREATED);
    }

    @PostMapping("/validate-delivery-info")
    public ResponseEntity<Void> validateDeliveryInfoEndpoint(@RequestBody OrderDTO orderInfo) {
        validateDeliveryInfo(orderInfo);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/calculate-shipping-fee")
    public ResponseEntity<Double> calculateShippingFee(@RequestBody OrderDTO orderInfo) {
        return new ResponseEntity<>(calculateShippingFeeInternal(orderInfo), HttpStatus.OK);
    }

    private void validateDeliveryInfo(OrderDTO orderInfo) {
        if (orderInfo.getCustomerName() == null || orderInfo.getCustomerName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        if (orderInfo.getCustomerPhone() == null || !orderInfo.getCustomerPhone().matches("^0\\d{9}$")) {
            throw new IllegalArgumentException("Invalid phone number. Must be 10 digits starting with 0");
        }
        if (orderInfo.getShippingAddress() == null || orderInfo.getShippingAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Shipping address is required");
        }
        if (orderInfo.getShippingProvince() == null || orderInfo.getShippingProvince().trim().isEmpty()) {
            throw new IllegalArgumentException("Shipping province is required");
        }
    }

    private double calculateShippingFeeInternal(OrderDTO orderInfo) {
        double totalWeight = 0.0;
        double totalPrice = 0.0;

        if (orderInfo.getItems() != null) {
            for (OrderDTO.OrderItemDTO item : orderInfo.getItems()) {
                com.team16.aims.entity.Media media = mediaRepo.findById(item.getMediaId())
                        .orElseThrow(() -> new RuntimeException("Media not found: " + item.getMediaId()));
                totalWeight += (media.getWeight() != null ? media.getWeight() : 0) * item.getQuantity();
                totalPrice += media.getPrice() * item.getQuantity();
            }
        }

        String province = orderInfo.getShippingProvince();
        boolean isInnerCity = province != null
                && (province.equalsIgnoreCase("Hà Nội") || province.equalsIgnoreCase("TP. Hồ Chí Minh"));

        double baseFee;
        double weightThreshold;

        if (isInnerCity) {
            baseFee = 22000;
            weightThreshold = 3.0;
        } else {
            baseFee = 30000;
            weightThreshold = 0.5;
        }

        double fee = baseFee;

        if (totalWeight > weightThreshold) {
            double extraWeight = totalWeight - weightThreshold;
            fee += Math.ceil(extraWeight / 0.5) * 2500;
        }

        // Shipping fee support
        if (totalPrice > 100000) {
            double support = 25000;
            if (fee <= support) {
                fee = 0;
            } else {
                fee -= support;
            }
        }

        return fee;
    }
}
