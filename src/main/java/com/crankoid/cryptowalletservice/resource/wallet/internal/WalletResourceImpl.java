package com.crankoid.cryptowalletservice.resource.wallet.internal;

import com.crankoid.cryptowalletservice.resource.wallet.api.WalletResource;
import com.crankoid.cryptowalletservice.resource.wallet.api.dao.WalletSeed;
import com.crankoid.cryptowalletservice.resource.wallet.api.dto.BalanceDTO;
import com.crankoid.cryptowalletservice.resource.wallet.api.dto.UserId;
import com.crankoid.cryptowalletservice.resource.wallet.api.dto.WalletDTO;
import com.crankoid.cryptowalletservice.service.blockchain.BlockchainService;
import com.crankoid.cryptowalletservice.service.wallet.WalletService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedOutputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import org.bitcoinj.wallet.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.protobuf.ProtobufEncoder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import static com.google.protobuf.ByteString.newOutput;

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
    public WalletDTO generateWallet(UserId userId) {
        System.out.println("userid: " + userId);
        if (!StringUtils.hasLength(userId.getUserId()) || userId.getUserId().length() != 6) {
            throw new IllegalArgumentException("illegal length of userId");
        }
        try {
            Wallet wallet = walletService.createNewWallet();
            blockchainService.replayBlockchain(wallet, String.format("BitcoinWallet-%s", userId.getUserId()));
            wallet.saveToFile(new File(String.format("BitcoinWallet-%s", userId.getUserId())));
            return convertWallet(wallet, userId.getUserId());
        } catch (IOException e) {
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
        System.out.println(wallet.toString());
        return walletDTO;
    }
}
