package com.team16.aims.controller;

import com.team16.aims.dto.OrderDTO;
import com.team16.aims.entity.Media;
import com.team16.aims.repository.MediaRepo;
import com.team16.aims.service.CartService;
import com.team16.aims.service.OrderService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    /**
     * Test tổng hợp cho tính phí vận chuyển.
     * Cấu trúc CSV: Province, Weight, UnitPrice, Quantity, ExpectedFee
     * nullValues = "null" cho phép truyền giá trị null vào Weight
     */
    @ParameterizedTest(name = "{index} => Prov={0}, W={1}, Price={2}, Qty={3} -> Expect={4}")
    @CsvSource(value = {
            // 1. Khu vực Hà Nội/HCM (Nội thành)
            "Hà Nội,   1.0,  50000, 1, 22000.0", // TC: < 3kg, < 100k
            "Hà Nội,   3.0,  50000, 1, 22000.0", // TC: = 3kg (Biên)
            "Hà Nội,   4.0,  50000, 1, 27000.0", // TC: > 3kg (4kg = 3kg + 2*0.5kg -> 22k + 5k)
            "hà nội,   1.0,  50000, 1, 22000.0", // TC: Case Insensitive Province
            "Hà Nội,   3.1,  50000, 1, 24500.0", // TC: Làm tròn khối lượng (0.1kg tính là 0.5kg)
            "Hà Nội,  null,  50000, 1, 22000.0", // TC: Null weight -> mặc định nhẹ nhất

            // 2. Khu vực Khác (Ngoại thành/Tỉnh khác)
            "Đà Nẵng,  0.4,  50000, 1, 30000.0", // TC: < 0.5kg
            "Đà Nẵng,  0.5,  50000, 1, 30000.0", // TC: = 0.5kg (Biên)
            "Đà Nẵng,  1.0,  50000, 1, 32500.0", // TC: > 0.5kg (1kg = 0.5kg + 1*0.5kg -> 30k + 2.5k)

            // 3. Logic hỗ trợ giá (Free ship/Giảm giá) - Tổng đơn > 100k
            "Hà Nội,   1.0, 200000, 1,     0.0", // TC: Phí 22k, Giảm max 25k -> Còn 0đ
            "Hà Nội,   1.0,  50000, 2, 22000.0", // TC: Tổng 100k (50k*2) -> Không giảm (Biên dưới)
            "Đà Nẵng, 10.0, 200000, 1, 52500.0" // TC: Phí 77.5k, Giảm 25k -> Còn 52.5k
    }, nullValues = "null")
    public void testCalculateShippingFee(String province, Float weight, double price, int quantity,
            double expectedFee) {
        // Arrange
        int mediaId = 1;
        OrderDTO.OrderItemDTO item = new OrderDTO.OrderItemDTO();
        item.setMediaId(mediaId);
        item.setQuantity(quantity);

        OrderDTO order = new OrderDTO();
        order.setShippingProvince(province);
        order.setItems(Collections.singletonList(item));

        Media media = new Media();
        media.setWeight(weight);
        media.setPrice(price);

        when(mediaRepo.findById(mediaId)).thenReturn(Optional.of(media));

        // Act
        ResponseEntity<Double> response = placeOrderController.calculateShippingFee(order);

        // Assert
        assertEquals(expectedFee, response.getBody());
    }
}