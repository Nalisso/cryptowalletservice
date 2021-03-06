package com.crankoid.cryptowalletservice.resource.wallet.internal;

import com.crankoid.cryptowalletservice.resource.wallet.api.WalletResource;
import com.crankoid.cryptowalletservice.resource.wallet.api.dto.BalanceDTO;
import com.crankoid.cryptowalletservice.resource.wallet.api.dto.UserId;
import com.crankoid.cryptowalletservice.resource.wallet.api.dto.WalletDTO;
import com.crankoid.cryptowalletservice.service.blockchain.BlockchainService;
import com.crankoid.cryptowalletservice.service.wallet.WalletService;
import org.bitcoinj.wallet.Wallet;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class WalletResourceImpl implements WalletResource {

    private final WalletService walletService;
    private final BlockchainService blockchainService;

    public WalletResourceImpl(WalletService walletService,
                              BlockchainService blockchainService) {
        this.walletService = walletService;
        this.blockchainService = blockchainService;
    }

    @Override
    public void generateWallet(UserId userId) {
        if (!StringUtils.hasLength(userId.getUserId()) || userId.getUserId().length() != 6) {
            throw new IllegalArgumentException("Illegal length of userId");
        }
        Wallet wallet = walletService.createNewWallet(userId.getUserId().toLowerCase());
        //Creates a local copy of the blockchain only containing blocks relevant for this wallet
        blockchainService.replayBlockchain(wallet, userId.getUserId().toLowerCase());
    }

    @Override
    public WalletDTO getWallet(String userId) {
        Wallet wallet = blockchainService.replayBlockchain(walletService.getWallet(userId.toLowerCase()), userId.toLowerCase());
        return convertWallet(wallet, userId.toLowerCase());
    }

    public boolean deleteWallet(String userId) {
        return walletService.deleteWallet(userId.toLowerCase());
    }

    private WalletDTO convertWallet(Wallet wallet, String userId) {
        WalletDTO walletDTO = new WalletDTO(
                new BalanceDTO(wallet),
                userId.toLowerCase(),
                wallet);
        System.out.println(wallet.toString());
        return walletDTO;
    }
}
