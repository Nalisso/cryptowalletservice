package com.crankoid.cryptowalletservice.resource.wallet.api;

import com.crankoid.cryptowalletservice.resource.wallet.api.dto.WalletDTO;
import com.crankoid.cryptowalletservice.resource.wallet.api.dto.UserId;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigInteger;

@RequestMapping("/wallet")
public interface WalletResource {

    @PostMapping
    String generateWallet(@RequestBody(required = true) UserId userId);

    @GetMapping
    WalletDTO getWallet(@RequestBody(required = true) UserId userId);

    @PostMapping("/send")
    String sendBitcoinPayment(@RequestBody(required = true) String sourceUserId,
                              @RequestBody(required = true) String destinationUserId,
                              @RequestBody(required = true) String satoshiAmount);


}
