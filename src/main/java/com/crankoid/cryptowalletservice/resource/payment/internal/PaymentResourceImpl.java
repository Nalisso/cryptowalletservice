package com.crankoid.cryptowalletservice.resource.payment.internal;

import com.crankoid.cryptowalletservice.resource.payment.api.PaymentResource;
import com.crankoid.cryptowalletservice.resource.payment.api.dto.PaymentDTO;
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
    public String sendBitcoinPayment(PaymentDTO paymentDTO) {
        try {
            Wallet walletSend = walletService.getWalletFromUserId(paymentDTO.getSourceUserId());
            Wallet walletReceive = walletService.getWalletFromUserId(paymentDTO.getDestinationUserId());
            Address targetAddress = walletReceive.currentReceiveAddress();
            Coin amount = Coin.parseCoin(paymentDTO.getSatoshis());
            Wallet.SendResult result = walletSend.sendCoins(blockchainService.getPeerGroup(), targetAddress, amount);
            TransactionBroadcast transactionBroadcast = result.broadcast;
            return "OK";
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
            return "Insufficient Money :(";
        }
    }
}
