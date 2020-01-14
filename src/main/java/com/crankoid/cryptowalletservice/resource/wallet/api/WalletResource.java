package com.crankoid.cryptowalletservice.resource.wallet.api;


import com.crankoid.cryptowalletservice.resource.wallet.api.dto.WalletDTO;
import com.crankoid.cryptowalletservice.resource.wallet.api.dto.UserId;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/wallet")
public interface WalletResource {

    @PostMapping
    void generateWallet(@RequestBody(required = true) UserId userId);

    @GetMapping("/{userId}")
    WalletDTO getWallet(@PathVariable(required = true) String userId);

    @DeleteMapping("/{userId}")
    boolean deleteWallet(@PathVariable(required = true) String userId);
}
