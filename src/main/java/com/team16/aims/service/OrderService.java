package com.team16.aims.service;

import com.team16.aims.dto.OrderDTO;
import com.team16.aims.entity.Media;
import com.team16.aims.entity.Order;
import com.team16.aims.entity.OrderItem;
import com.team16.aims.exception.ResourceNotFoundException;
import com.team16.aims.repository.MediaRepo;
import com.team16.aims.repository.OrderItemRepo;
import com.team16.aims.repository.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private OrderItemRepo orderItemRepo;
    @Autowired
    private MediaRepo mediaRepo;

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setCustomerName(orderDTO.getCustomerName());
        order.setCustomerPhone(orderDTO.getCustomerPhone());
        order.setCustomerEmail(orderDTO.getCustomerEmail());
        order.setShippingAddress(orderDTO.getShippingAddress());
        order.setShippingProvince(orderDTO.getShippingProvince());
        order.setShippingInstructions(orderDTO.getShippingInstructions());
        order.setOrderStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        // Calculate totals
        double subtotal = 0;
        if (orderDTO.getItems() != null) {
            for (OrderDTO.OrderItemDTO itemDTO : orderDTO.getItems()) {
                Media media = mediaRepo.findById(itemDTO.getMediaId())
                        .orElseThrow(() -> new ResourceNotFoundException("Media not found: " + itemDTO.getMediaId()));
                subtotal += media.getPrice() * itemDTO.getQuantity();
            }
        }

        order.setSubtotal(subtotal);
        order.setShippingFee(orderDTO.getShippingFee() != null ? orderDTO.getShippingFee() : 0.0);
        order.setVat(subtotal * 0.1); // Assuming 10% VAT
        order.setTotalAmount(order.getSubtotal() + order.getShippingFee() + order.getVat());

        order = orderRepo.save(order);

        if (orderDTO.getItems() != null) {
            for (OrderDTO.OrderItemDTO itemDTO : orderDTO.getItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                Media media = mediaRepo.findById(itemDTO.getMediaId())
                        .orElseThrow(() -> new ResourceNotFoundException("Media not found: " + itemDTO.getMediaId()));
                orderItem.setMedia(media);
                orderItem.setQuantity(itemDTO.getQuantity());
                orderItem.setPriceAtPurchase(media.getPrice());
                orderItemRepo.save(orderItem);
            }
        }

        return convertToDTO(order);
    }

    public OrderDTO getOrderById(Integer id) {
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return convertToDTO(order);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setCustomerName(order.getCustomerName());
        dto.setCustomerPhone(order.getCustomerPhone());
        dto.setCustomerEmail(order.getCustomerEmail());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setShippingProvince(order.getShippingProvince());
        dto.setShippingInstructions(order.getShippingInstructions());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setSubtotal(order.getSubtotal());
        dto.setShippingFee(order.getShippingFee());
        dto.setVat(order.getVat());
        dto.setTotalAmount(order.getTotalAmount());

        // We need to fetch items manually if not eager loaded or if we want to map them
        // Order entity has @OneToMany List<OrderItem> items
        if (order.getItems() != null) {
            List<OrderDTO.OrderItemDTO> itemDTOs = order.getItems().stream().map(item -> {
                return new OrderDTO.OrderItemDTO(
                        item.getMedia().getMediaId(),
                        item.getQuantity(),
                        item.getPriceAtPurchase());
            }).collect(Collectors.toList());
            dto.setItems(itemDTOs);
        }

        return dto;
    }
}
