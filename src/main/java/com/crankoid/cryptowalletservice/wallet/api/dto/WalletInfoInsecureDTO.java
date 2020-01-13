package com.crankoid.cryptowalletservice.wallet.api.dto;

public class WalletInfoInsecureDTO {
    private WalletInfoDTO walletInfoDTO;
    private String privateKey;

    public WalletInfoDTO getWalletInfoDTO() {
        return walletInfoDTO;
    }

    public void setWalletInfoDTO(WalletInfoDTO walletInfoDTO) {
        this.walletInfoDTO = walletInfoDTO;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }
}
