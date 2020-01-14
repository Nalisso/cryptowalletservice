package com.crankoid.cryptowalletservice.resource.payment.api;

import com.crankoid.cryptowalletservice.resource.payment.api.dto.PaymentDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/payment")
public interface PaymentResource {
    @PostMapping
    String sendBitcoinPayment(@RequestBody(required = true)PaymentDTO paymentDTO);
}
