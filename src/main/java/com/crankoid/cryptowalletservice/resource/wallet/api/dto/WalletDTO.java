package com.crankoid.cryptowalletservice.resource.wallet.api.dto;

import org.bitcoinj.wallet.Wallet;

import java.util.ArrayList;
import java.util.List;

public class WalletDTO {

    private BalanceDTO balance;
    private String address;
    private String userId;
    private List<TransactionDTO> transactions;

    public WalletDTO(BalanceDTO balance, String userId, Wallet wallet) {
        this.balance = balance;
        this.address = wallet.currentReceiveAddress().toString();
        this.userId = userId;
        List<TransactionDTO> transactions = new ArrayList<>();
        wallet.getTransactions(false).iterator().forEachRemaining(tx -> transactions.add(
                new TransactionDTO(
                        tx.getTxId().toString(),
                        "" + tx.getOutputSum().toFriendlyString(),
                        "" + ((tx.getFee() != null) ? tx.getFee().toFriendlyString() : "N/A"))));
        this.transactions = transactions;
    }

    public List<TransactionDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionDTO> transactions) {
        this.transactions = transactions;
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
