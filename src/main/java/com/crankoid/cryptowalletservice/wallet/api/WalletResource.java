package com.crankoid.cryptowalletservice.wallet.api;

import com.crankoid.cryptowalletservice.wallet.api.dto.WalletDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigInteger;

@RequestMapping("/wallet")
public interface WalletResource {

    String initBlockchain();

    @PostMapping("/generate")
    String generateWallet(String userId);

    @PostMapping("/information")
    WalletDTO getWalletInformation(String userId);

    @PostMapping("/send")
    String sendBitcoinPayment(String sourceUserId, String destinationUserId, BigInteger satoshiAmount);


}
