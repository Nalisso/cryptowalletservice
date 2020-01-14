package com.crankoid.cryptowalletservice.wallet.api.dto;

public class BalanceDTO {
    private long available;
    private long estimated;

    public long getAvailable() {
        return available;
    }

    public void setAvailable(long available) {
        this.available = available;
    }

    public long getEstimated() {
        return estimated;
    }

    public void setEstimated(long estimated) {
        this.estimated = estimated;
    }
}
