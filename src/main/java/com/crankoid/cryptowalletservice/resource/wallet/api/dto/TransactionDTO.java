package com.crankoid.cryptowalletservice.resource.wallet.api.dto;

public class TransactionDTO {
    private String transactionId;
    private String sent;
    private String received;
    private String fee;

    public TransactionDTO(String transactionId, String sent, String received, String fee) {
        this.transactionId = transactionId;
        this.sent = sent;
        this.received = received;
        this.fee = fee;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getSent() {
        return sent;
    }

    public void setSent(String sent) {
        this.sent = sent;
    }

    public String getReceived() {
        return received;
    }

    public void setReceived(String received) {
        this.received = received;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }
}
