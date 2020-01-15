package com.crankoid.cryptowalletservice.resource.wallet.internal;

import com.crankoid.cryptowalletservice.resource.wallet.api.WalletResource;
import com.crankoid.cryptowalletservice.resource.wallet.api.dto.BalanceDTO;
import com.crankoid.cryptowalletservice.resource.wallet.api.dto.UserId;
import com.crankoid.cryptowalletservice.resource.wallet.api.dto.WalletDTO;
import com.crankoid.cryptowalletservice.resource.wallet.internal.utilities.PersonalWallet;
import com.crankoid.cryptowalletservice.service.blockchain.BlockchainService;
import com.crankoid.cryptowalletservice.service.wallet.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bitcoinj.wallet.Wallet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class WalletResourceImpl implements WalletResource {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    static ObjectMapper mapper = new ObjectMapper();

    private final JdbcTemplate jdbcTemplate;
    private final WalletService walletService;
    private final BlockchainService blockchainService;

    public WalletResourceImpl(JdbcTemplate jdbcTemplate,
                              WalletService walletService,
                              BlockchainService blockchainService) {
        this.jdbcTemplate = jdbcTemplate;
        this.walletService = walletService;
        this.blockchainService = blockchainService;
    }

    @Override
    public void generateWallet(UserId userId) {
        System.out.println("userid: " + userId);
        if (!StringUtils.hasLength(userId.getUserId()) || userId.getUserId().length() != 6) {
            throw new IllegalArgumentException("illegal length of userId");
        }
        Wallet wallet = walletService.createNewWallet(userId.getUserId());
        blockchainService.replayBlockchain(wallet, userId.getUserId());
        PersonalWallet.save(userId.getUserId(), wallet);
        System.out.println("From genwallet resource: " + wallet.toString());
    }

    @Override
    public WalletDTO getWallet(String userId) {
        Wallet wallet = walletService.getWallet(userId);
        blockchainService.replayBlockchain(wallet, userId);
        return convertWallet(wallet, userId);
    }

    public boolean deleteWallet(String userId) {
        int affectedRows = jdbcTemplate.update("DELETE FROM wallet WHERE refId = ?", userId);
        return affectedRows > 0;
    }

    private WalletDTO convertWallet(Wallet wallet, String userId) {
        WalletDTO walletDTO = new WalletDTO();
        BalanceDTO balanceDTO = new BalanceDTO();
        walletDTO.setAddress(wallet.currentReceiveAddress().toString());
        balanceDTO.setAvailable(wallet.getBalance(Wallet.BalanceType.AVAILABLE).value);
        balanceDTO.setEstimated(wallet.getBalance(Wallet.BalanceType.ESTIMATED).value);
        walletDTO.setBalance(balanceDTO);
        walletDTO.setUserId(userId);
        System.out.println(wallet.toString());
        return walletDTO;
    }
}
