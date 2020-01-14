package com.crankoid.cryptowalletservice.resource.payment.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/payment")
public interface PaymentResource {
    @PostMapping
    String sendBitcoinPayment(@RequestBody(required = true) String sourceUserId,
                              @RequestBody(required = true) String destinationUserId,
                              @RequestBody(required = true) String satoshiAmount);
}
