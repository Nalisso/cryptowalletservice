package com.crankoid.cryptowalletservice.wallet.internal;

import com.crankoid.cryptowalletservice.wallet.api.WalletResource;
import com.crankoid.cryptowalletservice.wallet.api.dto.HelloWorldDTO;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class WalletResourceImpl implements WalletResource {

    @Override
    public HelloWorldDTO test() {
        HelloWorldDTO helloWorldDTO = new HelloWorldDTO();
        helloWorldDTO.setHello("World!");
        helloWorldDTO.setWorld("Hello");
        return helloWorldDTO;
    }
}
