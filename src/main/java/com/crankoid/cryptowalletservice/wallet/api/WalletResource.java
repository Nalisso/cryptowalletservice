package com.crankoid.cryptowalletservice.wallet.api;

import com.crankoid.cryptowalletservice.wallet.api.dto.WalletDTO;
import com.crankoid.cryptowalletservice.wallet.api.dto.UserId;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigInteger;

@RequestMapping("/wallet")
public interface WalletResource {

    @GetMapping("/initialize")
    String initBlockchainFile();

    @PostMapping("/generate")
    String generateWallet( @RequestBody(required = true) UserId userId);

    @GetMapping("/information")
    WalletDTO getWallet(String userId);

    @PostMapping("/send")
    String sendBitcoinPayment(@RequestBody(required = true) String sourceUserId,
                              @RequestBody(required = true) String destinationUserId,
                              @RequestBody(required = true) BigInteger satoshiAmount);


}
