package com.crankoid.cryptowalletservice.resource.wallet.api.dto;

public class TransactionDTO {
    private String transactionId;
    private String output;
    private String fee;

    public TransactionDTO(String transactionId, String output, String fee) {
        this.transactionId = transactionId;
        this.output = output;
        this.fee = fee;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }
}
