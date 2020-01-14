package com.crankoid.cryptowalletservice.service.wallet;

import com.crankoid.cryptowalletservice.resource.wallet.api.dao.WalletSeed;
import com.crankoid.cryptowalletservice.resource.wallet.internal.utilities.BitcoinNetwork;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@Service
public class WalletService {

    private final JdbcTemplate jdbcTemplate;
    static ObjectMapper mapper = new ObjectMapper();

    public WalletService(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public Wallet createNewWallet() {
        return Wallet.createDeterministic(BitcoinNetwork.get(), Script.ScriptType.P2PKH);
    }

    public Wallet getWalletFromUserId(String userId){
        try {
            String result = jdbcTemplate.queryForObject("SELECT keyValue FROM wallet WHERE refId = ?",
                    new Object[]{userId}, String.class);
            WalletSeed walletSeed = mapper.readValue(result, WalletSeed.class);
            DeterministicSeed seed = new DeterministicSeed(
                    walletSeed.getSeed(),
                    walletSeed.getMnemonicCode(),
                    walletSeed.getCreationTimeSeconds());
            return Wallet.fromSeed(BitcoinNetwork.get(), seed, Script.ScriptType.P2PKH);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        } catch(EmptyResultDataAccessException e) {
            throw new UserNotFoundException();
        }
    }

    public List<Wallet> getAllWallets(){
        try {
            List<String> result = jdbcTemplate.queryForList("SELECT keyValue FROM wallet", String.class);
            return convertToWallet(result);
        } catch(EmptyResultDataAccessException e) {
            throw new UserNotFoundException();
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
        if (wallets.isEmpty()){
            wallets.add(createNewWallet());
        }
        return wallets;
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "User not found")
    public class UserNotFoundException extends RuntimeException {
    }
}
