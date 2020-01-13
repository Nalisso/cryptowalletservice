package com.crankoid.cryptowalletservice.wallet.api;

import com.crankoid.cryptowalletservice.wallet.api.dto.HelloWorldDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/test")
public interface WalletResource {

    @GetMapping
    HelloWorldDTO test();

}
