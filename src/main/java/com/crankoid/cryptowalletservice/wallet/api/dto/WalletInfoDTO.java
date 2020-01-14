package com.crankoid.cryptowalletservice.wallet.api.dto;

public class WalletInfoDTO {
    private String address;
    private String publicKey;
    private Long availableSatoshis;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public Long getAvailableSatoshis() {
        return availableSatoshis;
    }

    public void setAvailableSatoshis(Long availableSatoshis) {
        this.availableSatoshis = availableSatoshis;
    }
}
