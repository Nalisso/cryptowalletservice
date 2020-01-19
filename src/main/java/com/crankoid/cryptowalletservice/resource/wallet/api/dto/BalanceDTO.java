package com.crankoid.cryptowalletservice.resource.wallet.api.dto;

public class BalanceDTO {
    private long available;
    private long estimated;

    public BalanceDTO() {
    }

    public BalanceDTO(long available, long estimated) {
        this.available = available;
        this.estimated = estimated;
    }

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
