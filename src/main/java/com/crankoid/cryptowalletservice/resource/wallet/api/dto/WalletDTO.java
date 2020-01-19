package com.crankoid.cryptowalletservice.resource.wallet.api.dto;

public class WalletDTO {

    private BalanceDTO balance;
    private String address;
    private String userId;

    public WalletDTO(BalanceDTO balance, String address, String userId) {
        this.balance = balance;
        this.address = address;
        this.userId = userId;
    }

    public WalletDTO() {
    }

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
}
