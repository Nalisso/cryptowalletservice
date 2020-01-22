package com.crankoid.cryptowalletservice.resource.payment.api.dto;

public class PaymentDTO {

    private String sourceUserId;
    private String destinationAddress;
    private String bitcoins;

    public String getSourceUserId() {
        return sourceUserId;
    }

    public void setSourceUserId(String sourceUserId) {
        this.sourceUserId = sourceUserId;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public String getBitcoins() {
        return bitcoins;
    }

    public void setBitcoins(String bitcoins) {
        this.bitcoins = bitcoins;
    }

}
