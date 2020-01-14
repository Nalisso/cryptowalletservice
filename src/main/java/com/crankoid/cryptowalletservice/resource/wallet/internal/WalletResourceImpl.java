package com.crankoid.cryptowalletservice.resource.wallet.internal;

import com.crankoid.cryptowalletservice.resource.wallet.api.WalletResource;
import com.crankoid.cryptowalletservice.resource.wallet.api.dao.WalletSeed;
import com.crankoid.cryptowalletservice.resource.wallet.api.dto.BalanceDTO;
import com.crankoid.cryptowalletservice.resource.wallet.api.dto.UserId;
import com.crankoid.cryptowalletservice.resource.wallet.api.dto.WalletDTO;
import com.crankoid.cryptowalletservice.resource.wallet.internal.utilities.BitcoinNetwork;
import com.crankoid.cryptowalletservice.service.blockchain.BlockchainService;
import com.crankoid.cryptowalletservice.service.wallet.WalletService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicSeed;
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

    public WalletResourceImpl(JdbcTemplate jdbcTemplate,
                              WalletService walletService) {
        this.jdbcTemplate = jdbcTemplate;
        this.walletService = walletService;
    }

    @Override
    public void generateWallet(UserId userId) {
        System.out.println("userid: " + userId);
        if (!StringUtils.hasLength(userId.getUserId()) || userId.getUserId().length() != 6) {
            throw new IllegalArgumentException("illegal length of userId");
        }
        try {
            Wallet wallet = walletService.createNewWallet();
            DeterministicSeed seed = wallet.getKeyChainSeed();
            WalletSeed walletSeed = new WalletSeed(seed.getMnemonicCode(), seed.getSeedBytes(), seed.getCreationTimeSeconds());
            //blockchainService.getPeerGroup().addWallet(wallet);
            jdbcTemplate.update(
                    "INSERT INTO wallet (refId, keyValue) VALUES(?,?)",
                    userId.getUserId(),
                    mapper.writeValueAsString(walletSeed));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public WalletDTO getWallet(String userId) {
        return convertWallet(walletService.getWalletFromUserId(userId), userId);
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
        return walletDTO;
    }
}
