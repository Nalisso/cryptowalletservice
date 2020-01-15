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
            Wallet senderWallet = walletService.getWallet(paymentDTO.getSourceUserId());
            Wallet receiverWallet = walletService.getWallet(paymentDTO.getDestinationUserId());
            Address targetAddress = senderWallet.currentReceiveAddress();
            Coin amount = Coin.parseCoin(paymentDTO.getSatoshis());


            Wallet.SendResult result = receiverWallet.sendCoins(null, targetAddress, amount); //SKA INTE VARE NULL
            TransactionBroadcast transactionBroadcast = result.broadcast;
            return "OK";
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
            return "Insufficient Money :(";
        }
    }
}
