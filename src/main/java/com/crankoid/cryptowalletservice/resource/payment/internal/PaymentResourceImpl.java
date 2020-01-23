package com.crankoid.cryptowalletservice.resource.payment.internal;

import com.crankoid.cryptowalletservice.resource.payment.api.PaymentResource;
import com.crankoid.cryptowalletservice.resource.payment.api.dto.FinishedPaymentDTO;
import com.crankoid.cryptowalletservice.resource.payment.api.dto.PaymentDTO;
import com.crankoid.cryptowalletservice.resource.wallet.api.dto.BalanceDTO;
import com.crankoid.cryptowalletservice.resource.wallet.api.dto.TransactionDTO;
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

    public PaymentResourceImpl(BlockchainService blockchainService) {
        this.blockchainService = blockchainService;
    }

    @Override
    public FinishedPaymentDTO sendBitcoinPayment(PaymentDTO paymentDTO) {
        try {
            Wallet senderWallet = PersonalWallet.load(paymentDTO.getSourceUserId().toLowerCase());
            Address targetAddress = LegacyAddress.fromString(BitcoinNetwork.get(), paymentDTO.getDestinationAddress());
            Coin amount = Coin.parseCoin(paymentDTO.getBitcoins());
            PeerGroup broadcaster = blockchainService.getPaymentPeerGroup(senderWallet, paymentDTO.getSourceUserId().toLowerCase());
            Wallet.SendResult result = senderWallet.sendCoins(broadcaster, targetAddress, amount);
            PersonalWallet.save(paymentDTO.getSourceUserId().toLowerCase(), senderWallet);
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
        WalletDTO returnSenderWallet = new WalletDTO(new BalanceDTO(senderWallet), paymentDTO.getSourceUserId().toLowerCase(), senderWallet);
        return new FinishedPaymentDTO(completeTransaction.getTxId().toString(), returnSenderWallet);
    }

    @ResponseStatus(code = HttpStatus.NOT_ACCEPTABLE, reason = "InsufficientFunds")
    public static class InsufficientFunds extends RuntimeException {
    }
}
