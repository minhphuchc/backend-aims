package com.team16.aims.controller;

import com.team16.aims.dto.OrderDTO;
import com.team16.aims.entity.Media;
import com.team16.aims.repository.MediaRepo;
import com.team16.aims.service.CartService;
import com.team16.aims.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlaceOrderControllerTest {

    @InjectMocks
    private PlaceOrderController placeOrderController;

    @Mock
    private MediaRepo mediaRepo;

    @Mock
    private CartService cartService;

    @Mock
    private OrderService orderService;

    @Test
    public void testCalculateShippingFee_Hanoi_Under3kg_Under100k() {
        // Arrange
        OrderDTO order = new OrderDTO();
        order.setShippingProvince("Hà Nội");
        OrderDTO.OrderItemDTO item = new OrderDTO.OrderItemDTO();
        item.setMediaId(1);
        item.setQuantity(1);
        order.setItems(Collections.singletonList(item));

        Media media = new Media();
        media.setWeight(1.0f); // 1kg
        media.setPrice(50000.0); // 50k
        when(mediaRepo.findById(1)).thenReturn(Optional.of(media));

        // Act
        ResponseEntity<Double> response = placeOrderController.calculateShippingFee(order);

        // Assert
        assertEquals(22000.0, response.getBody());
    }

    @Test
    public void testCalculateShippingFee_Hanoi_Over3kg_Under100k() {
        // Arrange
        OrderDTO order = new OrderDTO();
        order.setShippingProvince("Hà Nội");
        OrderDTO.OrderItemDTO item = new OrderDTO.OrderItemDTO();
        item.setMediaId(1);
        item.setQuantity(1);
        order.setItems(Collections.singletonList(item));

        Media media = new Media();
        media.setWeight(4.0f); // 4kg
        media.setPrice(50000.0); // 50k
        when(mediaRepo.findById(1)).thenReturn(Optional.of(media));

        // Act
        // Base: 22k. Extra: 4-3=1kg. 1/0.5 = 2 blocks. 2*2500 = 5000. Total: 27000.
        ResponseEntity<Double> response = placeOrderController.calculateShippingFee(order);

        // Assert
        assertEquals(27000.0, response.getBody());
    }

    @Test
    public void testCalculateShippingFee_OtherCity_Under05kg_Under100k() {
        // Arrange
        OrderDTO order = new OrderDTO();
        order.setShippingProvince("Đà Nẵng");
        OrderDTO.OrderItemDTO item = new OrderDTO.OrderItemDTO();
        item.setMediaId(1);
        item.setQuantity(1);
        order.setItems(Collections.singletonList(item));

        Media media = new Media();
        media.setWeight(0.4f); // 0.4kg
        media.setPrice(50000.0); // 50k
        when(mediaRepo.findById(1)).thenReturn(Optional.of(media));

        // Act
        // Base: 30k.
        ResponseEntity<Double> response = placeOrderController.calculateShippingFee(order);

        // Assert
        assertEquals(30000.0, response.getBody());
    }

    @Test
    public void testCalculateShippingFee_OtherCity_Over05kg_Under100k() {
        // Arrange
        OrderDTO order = new OrderDTO();
        order.setShippingProvince("Đà Nẵng");
        OrderDTO.OrderItemDTO item = new OrderDTO.OrderItemDTO();
        item.setMediaId(1);
        item.setQuantity(1);
        order.setItems(Collections.singletonList(item));

        Media media = new Media();
        media.setWeight(1.0f); // 1kg
        media.setPrice(50000.0); // 50k
        when(mediaRepo.findById(1)).thenReturn(Optional.of(media));

        // Act
        // Base: 30k. Extra: 1-0.5=0.5kg. 1 block. 2500. Total: 32500.
        ResponseEntity<Double> response = placeOrderController.calculateShippingFee(order);

        // Assert
        assertEquals(32500.0, response.getBody());
    }

    @Test
    public void testCalculateShippingFee_Support_FreeShipping() {
        // Arrange
        OrderDTO order = new OrderDTO();
        order.setShippingProvince("Hà Nội");
        OrderDTO.OrderItemDTO item = new OrderDTO.OrderItemDTO();
        item.setMediaId(1);
        item.setQuantity(1);
        order.setItems(Collections.singletonList(item));

        Media media = new Media();
        media.setWeight(1.0f); // 1kg
        media.setPrice(200000.0); // 200k > 100k
        when(mediaRepo.findById(1)).thenReturn(Optional.of(media));

        // Act
        // Fee: 22k. Support: 25k. Result: 0.
        ResponseEntity<Double> response = placeOrderController.calculateShippingFee(order);

        // Assert
        assertEquals(0.0, response.getBody());
    }

    @Test
    public void testCalculateShippingFee_Support_Partial() {
        // Arrange
        OrderDTO order = new OrderDTO();
        order.setShippingProvince("Đà Nẵng");
        OrderDTO.OrderItemDTO item = new OrderDTO.OrderItemDTO();
        item.setMediaId(1);
        item.setQuantity(1);
        order.setItems(Collections.singletonList(item));

        Media media = new Media();
        media.setWeight(10.0f); // 10kg
        media.setPrice(200000.0); // 200k > 100k
        when(mediaRepo.findById(1)).thenReturn(Optional.of(media));

        // Act
        // Base: 30k.
        // Extra: 10 - 0.5 = 9.5. 9.5/0.5 = 19 blocks. 19 * 2500 = 47500.
        // Total Fee: 30000 + 47500 = 77500.
        // Support: 25000.
        // Final: 77500 - 25000 = 52500.
        ResponseEntity<Double> response = placeOrderController.calculateShippingFee(order);

        // Assert
        assertEquals(52500.0, response.getBody());
    }

    @Test
    public void testCalculateShippingFee_Hanoi_Exact3kg() {
        OrderDTO order = new OrderDTO();
        order.setShippingProvince("Hà Nội");
        OrderDTO.OrderItemDTO item = new OrderDTO.OrderItemDTO();
        item.setMediaId(1);
        item.setQuantity(1);
        order.setItems(Collections.singletonList(item));

        Media media = new Media();
        media.setWeight(3.0f);
        media.setPrice(50000.0);
        when(mediaRepo.findById(1)).thenReturn(Optional.of(media));

        ResponseEntity<Double> response = placeOrderController.calculateShippingFee(order);
        assertEquals(22000.0, response.getBody());
    }

    @Test
    public void testCalculateShippingFee_Other_Exact05kg() {
        OrderDTO order = new OrderDTO();
        order.setShippingProvince("Đà Nẵng");
        OrderDTO.OrderItemDTO item = new OrderDTO.OrderItemDTO();
        item.setMediaId(1);
        item.setQuantity(1);
        order.setItems(Collections.singletonList(item));

        Media media = new Media();
        media.setWeight(0.5f);
        media.setPrice(50000.0);
        when(mediaRepo.findById(1)).thenReturn(Optional.of(media));

        ResponseEntity<Double> response = placeOrderController.calculateShippingFee(order);
        assertEquals(30000.0, response.getBody());
    }

    @Test
    public void testCalculateShippingFee_Exact100k_NoSupport() {
        OrderDTO order = new OrderDTO();
        order.setShippingProvince("Hà Nội");
        OrderDTO.OrderItemDTO item = new OrderDTO.OrderItemDTO();
        item.setMediaId(1);
        item.setQuantity(2); // 50k * 2 = 100k
        order.setItems(Collections.singletonList(item));

        Media media = new Media();
        media.setWeight(1.0f);
        media.setPrice(50000.0);
        when(mediaRepo.findById(1)).thenReturn(Optional.of(media));

        ResponseEntity<Double> response = placeOrderController.calculateShippingFee(order);
        assertEquals(22000.0, response.getBody());
    }

    @Test
    public void testCalculateShippingFee_NullWeight() {
        OrderDTO order = new OrderDTO();
        order.setShippingProvince("Hà Nội");
        OrderDTO.OrderItemDTO item = new OrderDTO.OrderItemDTO();
        item.setMediaId(1);
        item.setQuantity(1);
        order.setItems(Collections.singletonList(item));

        Media media = new Media();
        media.setWeight(null); // Null weight
        media.setPrice(50000.0);
        when(mediaRepo.findById(1)).thenReturn(Optional.of(media));

        ResponseEntity<Double> response = placeOrderController.calculateShippingFee(order);
        assertEquals(22000.0, response.getBody()); // Should treat as 0kg -> base fee
    }

    @Test
    public void testCalculateShippingFee_CaseInsensitiveProvince() {
        OrderDTO order = new OrderDTO();
        order.setShippingProvince("hà nội"); // lowercase
        OrderDTO.OrderItemDTO item = new OrderDTO.OrderItemDTO();
        item.setMediaId(1);
        item.setQuantity(1);
        order.setItems(Collections.singletonList(item));

        Media media = new Media();
        media.setWeight(1.0f);
        media.setPrice(50000.0);
        when(mediaRepo.findById(1)).thenReturn(Optional.of(media));

        ResponseEntity<Double> response = placeOrderController.calculateShippingFee(order);
        assertEquals(22000.0, response.getBody());
    }

    @Test
    public void testCalculateShippingFee_ExtraWeight_Rounding() {
        OrderDTO order = new OrderDTO();
        order.setShippingProvince("Hà Nội");
        OrderDTO.OrderItemDTO item = new OrderDTO.OrderItemDTO();
        item.setMediaId(1);
        item.setQuantity(1);
        order.setItems(Collections.singletonList(item));

        Media media = new Media();
        media.setWeight(3.1f); // 3.1kg -> 0.1kg extra -> 1 block
        media.setPrice(50000.0);
        when(mediaRepo.findById(1)).thenReturn(Optional.of(media));

        // Base 22k + 1 block * 2500 = 24500
        ResponseEntity<Double> response = placeOrderController.calculateShippingFee(order);
        assertEquals(24500.0, response.getBody());
    }
}
