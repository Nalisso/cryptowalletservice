package com.crankoid.cryptowalletservice.resource.payment.internal;

import com.crankoid.cryptowalletservice.resource.payment.api.PaymentResource;
import com.crankoid.cryptowalletservice.service.blockchain.BlockchainService;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.TransactionBroadcast;
import org.bitcoinj.wallet.Wallet;

public class PaymentResourceImpl implements PaymentResource {
    private final BlockchainService blockchainService;

    public PaymentResourceImpl(BlockchainService blockchainService) {
        this.blockchainService = blockchainService;
    }

    @Override
    public String sendBitcoinPayment(String sourceUserId, String destinationUserId, String satoshiAmount) {
        try {
            Wallet walletSend = getWalletFromUserId(sourceUserId);
            Wallet walletReceive = getWalletFromUserId(destinationUserId);
            Address targetAddress = walletReceive.currentReceiveAddress();
            Coin amount = Coin.parseCoin(satoshiAmount);
            Wallet.SendResult result = walletSend.sendCoins(blockchainService.getPeerGroup(), targetAddress, amount);
            TransactionBroadcast transactionBroadcast = result.broadcast;
            return "OK";
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
            return "Insufficient Money :(";
        }
    }

    private Wallet getWalletFromUserId(String userId) {
        return null;
    }
}
