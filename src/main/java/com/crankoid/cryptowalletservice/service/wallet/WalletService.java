package com.crankoid.cryptowalletservice.service.wallet;

import com.crankoid.cryptowalletservice.resource.wallet.internal.utilities.BitcoinNetwork;
import com.crankoid.cryptowalletservice.resource.wallet.internal.utilities.PersonalWallet;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.Wallet;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WalletService {

    private Map<String, Wallet> wallets;
    private final JdbcTemplate jdbcTemplate;

    public WalletService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        wallets = initializeWallets();
    }

    public Wallet createNewWallet(String userId) {
        List<String> userIds = jdbcTemplate.queryForList("SELECT userId FROM registeredUsers", String.class);
        if (userIds.contains(userId)) {
            throw new TooManyWalletsException();
        }
        jdbcTemplate.update(
                "INSERT INTO registeredUsers (userId) VALUES(?)",
                userId);
        Wallet wallet = Wallet.createDeterministic(BitcoinNetwork.get(), Script.ScriptType.P2PKH);
        PersonalWallet.save(userId, wallet);
        wallets.put(userId, wallet);
        return wallet;
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
            return PersonalWallet.remove(userId);
        } else {
            throw new UserNotFoundException();
        }
    }

    public Map<String, Wallet> getAllWallets() {
        return wallets;
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "User not found")
    public static class UserNotFoundException extends RuntimeException {
    }

    @ResponseStatus(code = HttpStatus.UNAUTHORIZED, reason = "User already has a wallet")
    public static class TooManyWalletsException extends RuntimeException {
    }

    private Map<String, Wallet> initializeWallets() {
        Map<String, Wallet> wallets = new HashMap<>();
        List<String> userIds = jdbcTemplate.queryForList("SELECT userId FROM registeredUsers", String.class);
        userIds.forEach(userId -> wallets.put(userId, PersonalWallet.load(userId)));
        return wallets;
    }
}
