package com.crankoid.cryptowalletservice.wallet.api;

import com.crankoid.cryptowalletservice.wallet.api.dto.UserId;
import com.crankoid.cryptowalletservice.wallet.api.dto.WalletInfoDTO;
import com.crankoid.cryptowalletservice.wallet.api.dto.WalletInfoInsecureDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigInteger;

@RequestMapping("/wallet")
public interface WalletResource {

    @PostMapping("/generate")
    WalletInfoInsecureDTO generateWallet( @RequestBody(required = true) UserId userId);

    @GetMapping("/wallet")
    WalletInfoInsecureDTO getWallet(String userId);


    @PostMapping("/information")
    WalletInfoDTO getWalletInformation(String userId);

    @PostMapping("/send")
    String sendBitcoinPayment(String sourceUserId, String destinationUserId, BigInteger satoshiAmount);


}
