package com.crankoid.cryptowalletservice.resource.wallet.api.dto;

public class WalletDTO {
    private BalanceDTO balance;

    public BalanceDTO getBalance() {
        return balance;
    }

    public void setBalance(BalanceDTO balance) {
        this.balance = balance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    private String address;
    private String userId;
}
