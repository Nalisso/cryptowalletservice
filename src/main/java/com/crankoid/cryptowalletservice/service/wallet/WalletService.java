package com.crankoid.cryptowalletservice.service.wallet;

import com.crankoid.cryptowalletservice.resource.wallet.api.dao.WalletSeed;
import com.crankoid.cryptowalletservice.resource.wallet.internal.utilities.BitcoinNetwork;
import com.crankoid.cryptowalletservice.service.blockchain.BlockchainService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedOutputStream;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.*;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.google.protobuf.ByteString.newOutput;

@Service
public class WalletService {

    private final JdbcTemplate jdbcTemplate;
    static ObjectMapper mapper = new ObjectMapper();
    private BlockchainService blockchainService;

    public WalletService(JdbcTemplate jdbcTemplate,
                         BlockchainService blockchainService){
        this.jdbcTemplate = jdbcTemplate;
        this.blockchainService = blockchainService;
    }

    public Wallet createNewWallet() {
        return Wallet.createDeterministic(BitcoinNetwork.get(), Script.ScriptType.P2PKH);
    }

    public Wallet getWalletFromUserId(String userId){
        try {
            Wallet wallet = Wallet.loadFromFile(new File(String.format("BitcoinWallet-%s", userId)));
            blockchainService.replayBlockchain(wallet, String.format("BitcoinWallet-%s", userId));
            wallet.saveToFile(new File(String.format("BitcoinWallet-%s", userId)));
            return wallet;
        } catch (UnreadableWalletException | IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public List<Wallet> getAllWallets(){
        try {
            return Arrays.asList(Wallet.loadFromFile(new File("BitcoinWallet-mada42")));
        } catch (UnreadableWalletException e) {
            e.printStackTrace();
            throw new IllegalStateException(e);
        }
    }

    private List<Wallet> convertToWallet(List<String> resultSet){
        List<Wallet> wallets = new ArrayList<>();
        resultSet.stream().forEach(result -> {
            try {
                WalletSeed walletSeed = mapper.readValue(result, WalletSeed.class);
                DeterministicSeed seed = new DeterministicSeed(
                        walletSeed.getSeed(),
                        walletSeed.getMnemonicCode(),
                        walletSeed.getCreationTimeSeconds());
                wallets.add(Wallet.fromSeed(BitcoinNetwork.get(), seed, Script.ScriptType.P2PKH));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        return wallets;
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "User not found")
    public class UserNotFoundException extends RuntimeException {
    }
}
