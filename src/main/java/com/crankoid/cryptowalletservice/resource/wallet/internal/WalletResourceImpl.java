package com.crankoid.cryptowalletservice.resource.wallet.internal;

import com.crankoid.cryptowalletservice.resource.wallet.api.WalletResource;
import com.crankoid.cryptowalletservice.resource.wallet.api.dao.WalletSeed;
import com.crankoid.cryptowalletservice.resource.wallet.api.dto.BalanceDTO;
import com.crankoid.cryptowalletservice.resource.wallet.api.dto.UserId;
import com.crankoid.cryptowalletservice.resource.wallet.api.dto.WalletDTO;
import com.crankoid.cryptowalletservice.resource.wallet.internal.utilities.NetworkStrategy;
import com.crankoid.cryptowalletservice.resource.wallet.internal.utilities.TestNetworkStrategy;
import com.crankoid.cryptowalletservice.service.blockchain.BlockchainService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.Wallet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;

@RestController()
public class WalletResourceImpl implements WalletResource {


    @Value("${spring.datasource.url}")
    private String dbUrl;
    private final NetworkStrategy networkStrategy = new TestNetworkStrategy();

    JdbcTemplate jdbcTemplate;
    static ObjectMapper mapper = new ObjectMapper();

    private final BlockchainService blockchainService;

    public WalletResourceImpl(JdbcTemplate jdbcTemplate,
                              BlockchainService blockchainService) {
        this.jdbcTemplate = jdbcTemplate;
        this.blockchainService = blockchainService;
    }

    @Override
    public String generateWallet(UserId userId) {
        System.out.println("userid: " + userId);
        if (!StringUtils.hasLength(userId.getUserId()) || userId.getUserId().length() != 6) {
            throw new IllegalArgumentException("illegal length of userId");
        }
        try {
            Wallet wallet = createNewWallet();
            DeterministicSeed seed = wallet.getKeyChainSeed();
            WalletSeed walletSeed = new WalletSeed(seed.getMnemonicCode(), seed.getSeedBytes(), seed.getCreationTimeSeconds());
            blockchainService.getPeerGroup().addWallet(wallet);
            jdbcTemplate.update(
                    "INSERT INTO wallet (refId, keyValue) VALUES(?,?)",
                    userId.getUserId(),
                    mapper.writeValueAsString(walletSeed));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
        return "OK";
    }

    @Override
    public WalletDTO getWallet(UserId userId) {
        return convertWallet(getWalletFromUserId(userId.getUserId()), userId.getUserId());
    }

    private Wallet getWalletFromUserId(String userId){
        String result = jdbcTemplate.queryForObject("SELECT keyValue FROM wallet WHERE refId = ?",
                new Object[]{userId}, String.class);
        try {
            WalletSeed walletSeed = mapper.readValue(result, WalletSeed.class);
            DeterministicSeed seed = new DeterministicSeed(
                    walletSeed.getSeed(),
                    walletSeed.getMnemonicCode(),
                    walletSeed.getCreationTimeSeconds());
            return Wallet.fromSeed(networkStrategy.getNetwork(), seed, Script.ScriptType.P2PKH);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
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

    @Override
    public String sendBitcoinPayment(String sourceUserId, String destinationUserId, String satoshiAmount) {
        try {
            Wallet walletSend = getWalletFromUserId(sourceUserId);
            Wallet walletReceive = getWalletFromUserId(destinationUserId);
            Address targetAddress = walletReceive.currentReceiveAddress();
            Coin amount = Coin.parseCoin(satoshiAmount);
            Wallet.SendResult result = walletSend.sendCoins(blockchainService.getPeerGroup(), targetAddress, amount);
            TransactionBroadcast transactionBroadcast = result.broadcast;
            return "OK";
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
            return "Insufficient Money :(";
        }
    }

    private Wallet createNewWallet() {
        return Wallet.createDeterministic(networkStrategy.getNetwork(), Script.ScriptType.P2PKH);
    }
}