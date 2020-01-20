package com.crankoid.cryptowalletservice.resource.wallet.api.dto;

public class UserId {

    private String userId;
    public String getUserId() {
        return userId.toLowerCase();
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
