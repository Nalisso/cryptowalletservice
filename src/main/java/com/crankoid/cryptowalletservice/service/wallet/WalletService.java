package com.crankoid.cryptowalletservice.service.wallet;

import com.crankoid.cryptowalletservice.resource.wallet.api.dao.WalletSeed;
import com.crankoid.cryptowalletservice.resource.wallet.internal.utilities.BitcoinNetwork;
import com.crankoid.cryptowalletservice.resource.wallet.internal.utilities.PersonalWallet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Service
public class WalletService {

    static ObjectMapper mapper = new ObjectMapper();
    private final JdbcTemplate jdbcTemplate;

    public WalletService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Wallet createNewWallet(String userId) {
        List<String> userIds = jdbcTemplate.queryForList("SELECT userId FROM registeredUsers", String.class);
        if (userIds.contains(userId)) {
            throw new TooManyWalletsException();
        }
        try {
        Wallet wallet = Wallet.createDeterministic(BitcoinNetwork.get(), Script.ScriptType.P2PKH);
        DeterministicSeed seed = wallet.getKeyChainSeed();
        WalletSeed walletSeed = new WalletSeed(seed.getMnemonicCode(), seed.getSeedBytes(), seed.getCreationTimeSeconds());
            jdbcTemplate.update(
                    "INSERT INTO registeredUsers (userId, walletBackup) VALUES(?, ?)",
                    userId,
                    mapper.writeValueAsString(walletSeed));
            PersonalWallet.save(userId, wallet);
            return wallet;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    public Wallet getWallet(String userId) {
        List<String> userIds = jdbcTemplate.queryForList("SELECT userId FROM registeredUsers", String.class);
        if (userIds.contains(userId)) {
            return PersonalWallet.load(userId);
        } else {
            throw new UserNotFoundException();
        }
    }

    public boolean deleteWallet(String userId) {
        List<String> userIds = jdbcTemplate.queryForList("SELECT userId FROM registeredUsers", String.class);
        if (userIds.contains(userId)) {
            int affectedRows = jdbcTemplate.update("DELETE FROM registeredUsers WHERE userId = ?", userId);
            return PersonalWallet.remove(userId) && affectedRows > 0;
        } else {
            throw new UserNotFoundException();
        }
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "User not found")
    public static class UserNotFoundException extends RuntimeException {
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "User already has a wallet")
    public static class TooManyWalletsException extends RuntimeException {
    }
}
