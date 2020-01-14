package com.crankoid.cryptowalletservice.resource.payment.api.dto;

public class PaymentDTO {

    private String sourceUserId;
    private String destinationUserId;
    private String satoshis;

    public String getSourceUserId() {
        return sourceUserId;
    }

    public void setSourceUserId(String sourceUserId) {
        this.sourceUserId = sourceUserId;
    }

    public String getDestinationUserId() {
        return destinationUserId;
    }

    public void setDestinationUserId(String destinationUserId) {
        this.destinationUserId = destinationUserId;
    }

    public String getSatoshis() {
        return satoshis;
    }

    public void setSatoshis(String satoshis) {
        this.satoshis = satoshis;
    }

}
