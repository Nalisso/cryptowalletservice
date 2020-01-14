package com.crankoid.cryptowalletservice.service.wallet;

import com.crankoid.cryptowalletservice.resource.wallet.api.dao.WalletSeed;
import com.crankoid.cryptowalletservice.resource.wallet.internal.utilities.BitcoinNetwork;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

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
        }
    }

}
