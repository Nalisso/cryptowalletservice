package com.crankoid.cryptowalletservice.wallet.api.dto;

public class WalletInfoDTO {
    private byte[] address;
    private String publicKey;
    private Long availableSatoshis;

    public byte[] getAddress() {
        return address;
    }

    public void setAddress(byte[] address) {
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
