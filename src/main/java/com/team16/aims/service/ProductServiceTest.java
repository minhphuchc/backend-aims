package com.hust.aims.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class ProductServiceTest {

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService();
    }

    @DisplayName("Validate Price Rules: 30% - 150% of Original Price")
    @ParameterizedTest(name = "Original={0}, Current={1} => Expected={2}")
    @CsvSource({
        "100000, 30000, true",   // TC_VP_01: Biên dưới 30%
        "100000, 150000, true",  // TC_VP_02: Biên trên 150%
        "100000, 100000, true",  // TC_VP_03: Hợp lệ
        "100000, 29999, false",  // TC_VP_04: Dưới 30%
        "100000, 150001, false", // TC_VP_05: Trên 150%
        "100000, -100, false",   // TC_VP_06: Số âm
        "0, 100, false"          // TC_VP_07: Giá gốc 0
    })
    void testValidateProductPrice(double original, double current, boolean expected) {
        // When
        boolean isValid = productService.validateProductPrice(original, current);
        
        // Then
        Assertions.assertEquals(expected, isValid);
    }
}