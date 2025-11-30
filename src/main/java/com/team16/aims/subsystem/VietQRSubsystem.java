package com.team16.aims.subsystem;

import com.team16.aims.entity.Order;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class VietQRSubsystem implements PaymentSubsystem {

    // Hardcoded for demo purposes. In a real app, these should be in properties.
    private static final String BANK_ID = "MB"; // Example: MB Bank
    private static final String ACCOUNT_NO = "0000000000"; // Example Account Number
    private static final String TEMPLATE = "compact"; // VietQR Template

    @Override
    public String generatePaymentUrl(Order order) {
        try {
            String addInfo = "Order " + order.getOrderId();
            String encodedAddInfo = URLEncoder.encode(addInfo, StandardCharsets.UTF_8);
            String accountName = "AIMS STORE";
            String encodedAccountName = URLEncoder.encode(accountName, StandardCharsets.UTF_8);

            // Format:
            // https://img.vietqr.io/image/<BANK_ID>-<ACCOUNT_NO>-<TEMPLATE>.png?amount=<AMOUNT>&addInfo=<INFO>&accountName=<NAME>
            return String.format("https://img.vietqr.io/image/%s-%s-%s.png?amount=%.0f&addInfo=%s&accountName=%s",
                    BANK_ID, ACCOUNT_NO, TEMPLATE, order.getTotalAmount(), encodedAddInfo, encodedAccountName);
        } catch (Exception e) {
            throw new RuntimeException("Error generating VietQR URL", e);
        }
    }
}
