package com.crankoid.cryptowalletservice.wallet.api;

import com.crankoid.cryptowalletservice.wallet.api.dto.WalletDTO;
import com.crankoid.cryptowalletservice.wallet.api.dto.UserId;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigInteger;

@RequestMapping("/wallet")
public interface WalletResource {

    @GetMapping("/initialize")
    String initBlockchainFile();

    @PostMapping
    String generateWallet(@RequestBody(required = true) UserId userId);

    @GetMapping
    WalletDTO getWallet(@RequestBody(required = true) UserId userId);

    @PostMapping("/send")
    String sendBitcoinPayment(@RequestBody(required = true) String sourceUserId,
                              @RequestBody(required = true) String destinationUserId,
                              @RequestBody(required = true) BigInteger satoshiAmount);


}
