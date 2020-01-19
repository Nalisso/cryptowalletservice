package com.crankoid.cryptowalletservice.resource.payment.internal;

import com.crankoid.cryptowalletservice.resource.payment.api.PaymentResource;
import com.crankoid.cryptowalletservice.resource.payment.api.dto.PaymentDTO;
import com.crankoid.cryptowalletservice.service.blockchain.BlockchainService;
import com.crankoid.cryptowalletservice.service.wallet.WalletService;
import org.bitcoinj.core.*;
import org.bitcoinj.wallet.Wallet;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

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
            Wallet senderWallet = walletService.getWallet(paymentDTO.getSourceUserId());
            Wallet receiverWallet = walletService.getWallet(paymentDTO.getDestinationUserId());
            Address targetAddress = senderWallet.currentReceiveAddress();
            Coin amount = Coin.parseCoin(paymentDTO.getSatoshis());
            PeerGroup broadcaster = blockchainService.getPaymentPeerGroup(senderWallet, paymentDTO.getSourceUserId());
            Wallet.SendResult result = receiverWallet.sendCoins(broadcaster, targetAddress, amount);
            Transaction completeTransaction = result.broadcastComplete.get();
            return completeTransaction.toString();
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
            throw new InsufficientFunds();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    @ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "InsufficientFunds")
    public static class InsufficientFunds extends RuntimeException {
    }
}
