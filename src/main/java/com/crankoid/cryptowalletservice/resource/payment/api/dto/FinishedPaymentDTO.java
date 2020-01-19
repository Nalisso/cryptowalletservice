package com.crankoid.cryptowalletservice.resource.payment.api.dto;

import com.crankoid.cryptowalletservice.resource.wallet.api.dto.WalletDTO;

public class FinishedPaymentDTO {

    private String transactionId;
    private WalletDTO senderWallet;

    public FinishedPaymentDTO(String transactionId, WalletDTO senderWallet){
        this.transactionId = transactionId;
        this.senderWallet = senderWallet;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public WalletDTO getSenderWallet() {
        return senderWallet;
    }

    public void setSenderWallet(WalletDTO senderWallet) {
        this.senderWallet = senderWallet;
    }
}
