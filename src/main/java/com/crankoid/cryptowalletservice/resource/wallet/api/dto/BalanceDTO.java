package com.crankoid.cryptowalletservice.resource.wallet.api.dto;

import org.bitcoinj.wallet.Wallet;

public class BalanceDTO {
    private String available;
    private String estimated;

    public BalanceDTO(Wallet wallet) {
        this.available = wallet.getBalance(Wallet.BalanceType.AVAILABLE).toFriendlyString();
        this.estimated = wallet.getBalance(Wallet.BalanceType.ESTIMATED).toFriendlyString();
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getEstimated() {
        return estimated;
    }

    public void setEstimated(String estimated) {
        this.estimated = estimated;
    }
}
