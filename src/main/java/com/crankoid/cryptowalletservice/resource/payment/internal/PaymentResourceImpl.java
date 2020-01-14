package com.crankoid.cryptowalletservice.resource.payment.internal;

import com.crankoid.cryptowalletservice.resource.payment.api.PaymentResource;
import com.crankoid.cryptowalletservice.service.blockchain.BlockchainService;
import com.crankoid.cryptowalletservice.service.wallet.WalletService;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.TransactionBroadcast;
import org.bitcoinj.wallet.Wallet;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PaymentResourceImpl implements PaymentResource {
    private final BlockchainService blockchainService;
    private final WalletService walletService;

    public PaymentResourceImpl(BlockchainService blockchainService,
                               WalletService walletService) {
        this.blockchainService = blockchainService;
        this.walletService = walletService;
    }

    @Override
    public String sendBitcoinPayment(String sourceUserId, String destinationUserId, String satoshiAmount) {
        try {
            Wallet walletSend = walletService.getWalletFromUserId(sourceUserId);
            Wallet walletReceive = walletService.getWalletFromUserId(destinationUserId);
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
}
