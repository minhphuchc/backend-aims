package com.team16.aims.controller;

import com.team16.aims.dto.PaymentTransactionDTO;
import com.team16.aims.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentTransactionDTO> processPayment(@RequestBody PaymentTransactionDTO transactionDTO) {
        return new ResponseEntity<>(paymentService.processPayment(transactionDTO), HttpStatus.CREATED);
    }

    @GetMapping("/url/{orderId}")
    public ResponseEntity<String> getPaymentUrl(@PathVariable Integer orderId) {
        return new ResponseEntity<>(paymentService.getPaymentUrl(orderId), HttpStatus.OK);
    }
}
