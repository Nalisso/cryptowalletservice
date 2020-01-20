package com.crankoid.cryptowalletservice.resource.payment.internal;

import com.crankoid.cryptowalletservice.resource.payment.api.PaymentResource;
import com.crankoid.cryptowalletservice.resource.payment.api.dto.FinishedPaymentDTO;
import com.crankoid.cryptowalletservice.resource.payment.api.dto.PaymentDTO;
import com.crankoid.cryptowalletservice.resource.wallet.api.dto.BalanceDTO;
import com.crankoid.cryptowalletservice.resource.wallet.api.dto.WalletDTO;
import com.crankoid.cryptowalletservice.resource.wallet.internal.utilities.BitcoinNetwork;
import com.crankoid.cryptowalletservice.resource.wallet.internal.utilities.PersonalWallet;
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
    public FinishedPaymentDTO sendBitcoinPayment(PaymentDTO paymentDTO) {
        try {
            Wallet senderWallet = walletService.getWallet(paymentDTO.getSourceUserId().toLowerCase());
            Address targetAddress = LegacyAddress.fromString(BitcoinNetwork.get(), paymentDTO.getDestinationAddress());
            Coin amount = Coin.valueOf(Long.getLong(paymentDTO.getSatoshis()));
            PeerGroup broadcaster = blockchainService.getPaymentPeerGroup(senderWallet, paymentDTO.getSourceUserId().toLowerCase());
            Wallet.SendResult result = senderWallet.sendCoins(broadcaster, targetAddress, amount);
            PersonalWallet.save(paymentDTO.getSourceUserId().toLowerCase(), senderWallet);
            broadcaster.stop();
            return getFinishedPaymentDTO(senderWallet, paymentDTO, result.broadcastComplete.get());
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
            throw new InsufficientFunds();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    private FinishedPaymentDTO getFinishedPaymentDTO(Wallet senderWallet, PaymentDTO paymentDTO, Transaction completeTransaction){
        BalanceDTO balanceDTO = new BalanceDTO(
                senderWallet.getBalance(Wallet.BalanceType.AVAILABLE).longValue(),
                senderWallet.getBalance(Wallet.BalanceType.ESTIMATED).longValue());
        WalletDTO returnSenderWallet = new WalletDTO(balanceDTO, senderWallet.currentReceiveAddress().toString(), paymentDTO.getSourceUserId().toLowerCase());
        return new FinishedPaymentDTO(completeTransaction.getTxId().toString(), returnSenderWallet);
    }

    @ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "InsufficientFunds")
    public static class InsufficientFunds extends RuntimeException {
    }
}
